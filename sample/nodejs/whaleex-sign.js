const ecc = require("eosjs-ecc");
const crypto = require("crypto");
const hash = crypto.createHash("sha256");
const fs = require("fs-extra");
const Long = require("long");
const math = require("mathjs");
math.config({ number: "BigNumber" });
const { HOST, ExEosAccount } = require("./config.json");
const DEFAULT = params => {
  const keys = getKeys();
  const { privateKey, publicKey: pk, APIKey } = keys;
  return {
    timestamp: new Date().getTime(),
    APIKey,
    pk,
    ...params
  };
};
const HANDLER = {
  "/api/v1/order/orders": DEFAULT,
  "/api/auth/pk/status": params => {
    const keys = getKeys();
    const { publicKey: pk } = keys;
    return {
      pk,
      ...params
    };
  },
  "/api/v1/order/matchresults": DEFAULT,
  DEFAULT
};
// 将各类数据打包成二进制流
const Packer = function() {
  this.buf = Buffer.alloc(0);
  this.updateStr = function(str) {
    this.buf = Buffer.concat([this.buf, Buffer.from(str)]);
    return this;
  };
  this.updateInt8 = function(i) {
    var tmp = Buffer.alloc(1);
    tmp.writeInt8(i, 0);
    this.buf = Buffer.concat([this.buf, tmp]);
    return this;
  };
  this.updateInt16 = function(i) {
    var tmp = Buffer.alloc(2);
    tmp.writeInt16LE(i, 0);
    this.buf = Buffer.concat([this.buf, tmp]);
    return this;
  };
  this.updateInt32 = function(i) {
    var tmp = Buffer.alloc(4);
    tmp.writeInt32LE(i, 0);
    this.buf = Buffer.concat([this.buf, tmp]);
    return this;
  };
  this.updateInt64 = function(str) {
    var tmp = Buffer.alloc(8);
    var long = Long.fromString(str, 10);
    this.buf = Buffer.concat([this.buf, Buffer.from(long.toBytesLE())]);
    return this;
  };
  this.finalize = function() {
    return this.buf;
  };
  this.clear = function() {
    this.buf = Buffer.alloc(0);
  };
};
function getHandle(path) {
  return HANDLER[path] || HANDLER.DEFAULT;
}
function getHash() {
  return crypto.createHash("sha256");
}
function multiply(m, n, decimal, ceil) {
  let result = math.eval(`${m} * ${n} * ${Math.pow(10, decimal)}`);
  if (ceil) {
    return String(Math.ceil(result));
  }
  return String(result).split(".")[0];
}
function getKeys() {
  const keys = fs.readFileSync("ApiKey.json", "utf8") || "{}";
  const keysObj = JSON.parse(keys);
  return keysObj;
}
function signOrder(orderId, post, timestamp, account, symbolObj) {
  const {
    baseToken = "IQ",
    quoteToken = "EOS",
    basePrecision = 3,
    quotePrecision = 4,
    baseContract,
    quoteContract
  } = symbolObj;
  // "IQ/EOS for example"
  var pack = new Packer();
  pack
    .updateStr(account)
    .updateStr(ExEosAccount)
    .updateInt64("" + orderId)
    .updateInt32(parseInt(timestamp / 1000));
  if (post.type == "buy-limit") {
    pack
      .updateStr(quoteContract)
      .updateStr(quoteToken)
      .updateInt64(
        multiply(post.price, post.amount, quotePrecision /*EOS链上精度*/, true)
      )
      .updateStr(baseContract)
      .updateStr(baseToken)
      .updateInt64(multiply(1, post.amount, basePrecision /*IQ链上精度*/));
  }
  if (post.type == "sell-limit") {
    pack
      .updateStr(baseContract)
      .updateStr(baseToken)
      .updateInt64(multiply(1, post.amount, basePrecision))
      .updateStr(quoteContract)
      .updateStr(quoteToken)
      .updateInt64(multiply(post.price, post.amount, quotePrecision));
  }
  if (post.type == "buy-market") {
    pack
      .updateStr(quoteContract)
      .updateStr(quoteToken)
      .updateInt64(multiply(1, post.amount, quotePrecision))
      .updateStr(baseContract)
      .updateStr(baseToken)
      .updateInt64("0");
  }
  if (post.type == "sell-market") {
    pack
      .updateStr(baseContract)
      .updateStr(baseToken)
      .updateInt64(multiply(1, post.amount, basePrecision))
      .updateStr(quoteContract)
      .updateStr(quoteToken)
      .updateInt64("0");
  }
  pack.updateInt16(10).updateInt16(10);

  var buf = pack.finalize();
  var hashData = getHash()
    .update(buf)
    .digest("hex");
  var pack1 = new Packer();
  let { privateKey } = getKeys();
  var sig = ecc.signHash(hashData, privateKey);
  return sig;
}
function sort(map) {
  var sortedKeys = [];
  for (i in map) {
    sortedKeys.push(i);
  }
  sortedKeys.sort();
  var params = "";
  for (i = 0; i < sortedKeys.length; i++) {
    params = params + "&" + sortedKeys[i] + "=" + map[sortedKeys[i]];
  }
  params = params.slice(1);
  return params;
}
function signData(method, path, params = {}) {
  let { privateKey } = getKeys();
  // console.log("signData", method, path, params);
  let map = getHandle(path)(params);
  var params = sort(map);
  var data = `${method.toUpperCase()}\napi.whaleex.com\n${path}\n${encodeURIComponent(
    params
  )}`;
  var sig = ecc.sign(data, privateKey);
  params += "&Signature=" + sig;
  return params;
}
function signDataOrder(orderId, order, account, symbolObj = {}) {
  let { APIKey, publicKey: pk } = getKeys();
  var map = {
    APIKey,
    timestamp: new Date().getTime(),
    pk,
    orderId: orderId
  };
  var params = sort(map);
  var sig = signOrder(orderId, order, map.timestamp, account, symbolObj);
  params = params + "&Signature=" + sig;
  return params;
}
module.exports = {
  signData,
  signOrder,
  sort,
  signDataOrder,
  getKeys
};
