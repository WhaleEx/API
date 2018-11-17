const ecc = require("eosjs-ecc");
const crypto = require("crypto");
const fs = require("fs-extra");
const path = require("path");
const hash = crypto.createHash("sha256");
const Qs = require("qs");
const Long = require("long");
const axios = require("axios");
const keys = require("./keys.json");
const API_K = require("./ApiKey.json");
const APIKey = API_K && API_K.APIKey;
const { HOST, AUTH_HOST, ExAccount } = require("./config.json");
const URL_prefix = "https://" + HOST + "/BUSINESS";
const AUTH_prefix = "https://" + AUTH_HOST + "/UAA";
const U = require("./whaleex-sign.js");
const { privateKey, publicKey } = keys;
function resetUserKey() {
  const newKeys = {...keys};
  delete newKeys.privateKey;
  delete newKeys.publicKey;
  fs.writeFileSync(`keys.json`, JSON.stringify({}), "utf8");
}
function resetApiKey() {
  fs.writeFileSync(`ApiKey.json`, JSON.stringify({}), "utf8");
}
async function generateKeys() {
  //请妥善保管生成后的私钥，不要泄露
  if (keys && keys.publicKey) {
    console.log("> 您已生成过公钥，若想重新生成，请调用 WAL.resetUserKey()");
  } else {
    const privateKey = await ecc.randomKey();
    const publicKey = ecc.privateToPublic(privateKey);
    fs.writeFileSync(
      `keys.json`,
      JSON.stringify({ privateKey, publicKey }),
      "utf8"
    );
    console.log("> 生成成功。请妥善保管生成后的私钥，不要泄露！！");
  }
}
async function registerPK(access_token) {
  var pkBindStatus = await checkPkBindStatus(access_token);
  if (pkBindStatus === "ACTIVED") {
    console.log(`> 本地公钥（已激活）： ${publicKey}`);
    return;
  }
  var path = "/api/account/pk4api";
  var map = {
    pk: publicKey
  };
  var params = U.sort(map);
  axios({
    method: "post",
    url: URL_prefix + path + "?" + params,
    headers: { Authorization: "bearer " + access_token }
  })
    .then(function(response) {
      console.log(
        "> 若未激活，请使用eos钱包，通过向合约小额打币的方式，完成公钥在合约的绑定，约3-5分钟"
      );
      console.log("> 收款合约账户：" + ExAccount);
      console.log("> 务必加上备注：" + "bind:" + publicKey + ":WhaleEx");
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getUserToken(username, password, countryCode) {
  var path = "/oauth/token";
  let params = {
    grant_type: "password",
    client_id: "client",
    client_secret: "secret",
    clientType: "api",
    source: "api",
    countryCode,
    username,
    password: hashPassword(password)
  };
  return axios({
    method: "post",
    url: AUTH_prefix + path,
    data: Qs.stringify(params),
    headers: { "Content-Type": "application/x-www-form-urlencoded" }
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log("login error", error.response.data);
    });
}
function getApiKey(access_token) {
  if (API_K && API_K.APIKey) {
    console.log(
      "> 您已生成API Key，若想重新生成，请手动删除文件内容 WAL.resetApiKey()"
    );
    return;
  }
  var path = "/api/user/apiKey";
  axios({
    method: "post",
    url: URL_prefix + path,
    headers: { Authorization: "bearer " + access_token }
  })
    .then(async function(response) {
      console.log(response.data.result);
      fs.writeFileSync(
        `ApiKey.json`,
        JSON.stringify({ APIKey: response.data.result }),
        "utf8"
      );
      console.log("API Key 生成成功。");
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function checkPkBindStatus(access_token) {
  var path = "/api/auth/pk/status";
  var params = U.signData("GET", path);
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params,
    headers: { Authorization: "bearer " + access_token }
  })
    .then(function(response) {
      console.log(response.data.result.status, publicKey);
      return response.data.result.status;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getGloableIds(remark, size = 50) {
  var path = "/api/v1/order/globalIds";
  var params = U.signData("GET", path, { size, remark });
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      return response.data.result;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getExecOrder() {
  var path = "/api/v1/order/matchresults";
  var params = U.signData("GET", path);
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(response.data.result.content);
      return response.data.result.content;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getOrderBook(symbol = "IQEBTC") {
  var path = "/api/public/v1/orderBook/" + symbol;
  axios({
    method: "get",
    url: URL_prefix + path
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getTicker(symbol) {
  var path = "/api/public/v1/ticker/" + symbol;
  console.log(path);
  axios({
    method: "get",
    url: URL_prefix + path
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function hashPassword(password) {
  const hash = crypto.createHash("sha256");
  hash.update(password);
  return hash.digest("hex");
}
function queryOrderHistory(
  symbol = "IQEBTC",
  startDate = "2018-11-07",
  endDate = "2018-11-09"
) {
  var path = "/api/v1/order/orders";
  var params = U.signData("GET", path, {
    symbol,
    "start-date": startDate,
    "end-date": endDate,
    page: 0,
    size: 10
  });
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function queryOrderDetail(orderId) {
  var path = "/api/v1/order/orders/" + orderId;
  var params = U.signData("GET", path);
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function queryOpenOrder(side = "buy") {
  var path = "/api/v1/order/openOrders";
  var params = U.signData("GET", path, { side });
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getHistoryOrder() {
  var path = "/api/v1/order/orders";
  var params = U.signData("GET", path);
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getOrderDetail(orderId) {
  var path = "/api/v1/order/orders/" + orderId;
  var params = U.signData("GET", path, { orderId });
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function getOpenOrders(page = 0, size = 10) {
  var path = "/api/v1/order/openOrders";
  var params = U.signData("GET", path, { page, size });
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log("> 当前委托：");
      function Order(symbolId, price, origQty, type, side) {
        this.symbolId = symbolId;
        this.price = price;
        this.origQty = origQty;
        this.type = type;
        this.side = side;
      }
      var orders = {};
      response.data.result.content.forEach(i => {
        const { orderId, symbolId, price, origQty, type, side } = i;
        orders[orderId] = new Order(symbolId, price, origQty, type, side);
      });
      console.table(orders);
      return response.data.result.content;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function queryMatchResults(
  types = "buy-limit",
  symbol = "IQEBTC",
  startDate = "2018-11-07",
  endDate = "2018-11-15",
  page = 0,
  size = 10
) {
  var path = "/api/v1/order/matchresults";
  var params = U.signData("GET", path, {
    symbol,
    types,
    "start-date": startDate,
    "end-date": endDate,
    page,
    size
  });
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function queryOneOrderMatchResults(orderId = "87263780807136789") {
  var path = "/api/v1/order/orders/" + orderId + "/matchresults";
  var params = U.signData("GET", path);
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function queryAssets() {
  var path = "/api/v1/assets";
  var params = U.signData("GET", path, { page: 0, size: 100 });
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log("> 您的资产：");
      function Asset(
        totalAmount,
        fixedAmount,
        availableAmount,
        privatePlacement
      ) {
        this.totalAmount = totalAmount;
        this.fixedAmount = fixedAmount;
        this.availableAmount = availableAmount;
        this.privatePlacement = privatePlacement;
      }
      var assets = {};
      response.data.result.list.content.forEach(i => {
        const {
          currency,
          totalAmount,
          fixedAmount,
          availableAmount,
          privatePlacement
        } = i;
        if (+totalAmount === 0) {
          return;
        }
        assets[currency] = new Asset(
          totalAmount,
          fixedAmount,
          availableAmount,
          privatePlacement
        );
      });
      console.table(assets);
      return response.data;
    })
    .catch(function(error) {
      console.log(error.response.data);
    });
}

function queryOneAsset(currency) {
  var path = "/api/v1/asset/" + currency;
  var params = U.signData("GET", path);
  axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function placeOrder(
  orderId,
  account, //your eos account
  order = {
    amount: "1",
    price: "999999",
    symbol: "IQEOS",
    type: "sell-limit"
  },
  symbolObj
) {
  var path = "/api/v1/order/orders/place";
  var params = U.signDataOrder(orderId, order, account, symbolObj);
  axios({
    method: "post",
    url: URL_prefix + path + "?" + params,
    data: order
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function cancelOrder(orderId) {
  var path = "/api/v1/order/orders/" + orderId + "/submitcancel";
  var params = U.signData("POST", path);
  axios({
    method: "post",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function cancelBatch(ids = ["1", "2", "3"]) {
  var path = "/api/v1/order/orders/batchcancel";
  var params = U.signData("POST", path);
  axios({
    method: "post",
    url: URL_prefix + path + "?" + params,
    data: {
      "order-ids": ids
    }
  })
    .then(function(response) {
      console.log(response);
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}
function cancelOpen() {
  var path = "/api/v1/order/orders/batchCancelOpenOrders";
  var params = U.signData("POST", path);
  axios({
    method: "post",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error);
    });
}

module.exports = {
  getUserToken, //登录获取token
  generateKeys, //生成私钥和公钥 会保存在keys.json中
  registerPK, //注册公钥
  checkPkBindStatus, //查询pk绑定状态
  getApiKey, //获取ApiKey 并保存在ApiKey.json中
  getGloableIds,
  placeOrder,
  getOpenOrders,
  cancelOrder,
  getOrderDetail,
  getHistoryOrder,
  getExecOrder,
  queryOneOrderMatchResults,
  queryAssets,
  queryOneAsset,
  getTicker,
  getOrderBook,
  queryOrderHistory,
  queryOrderDetail,
  queryOpenOrder,
  queryMatchResults,
  cancelOpen,
  cancelBatch,
  resetUserKey,
  resetApiKey
};
