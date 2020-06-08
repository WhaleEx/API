const ecc = require("eosjs-ecc");
const crypto = require("crypto");
const fs = require("fs-extra");
const path = require("path");
const hash = crypto.createHash("sha256");
const Qs = require("qs");
const Long = require("long");
const axios = require("axios");
const Print = require("./util/console.js");
const Config = require("./config.json");
const { HOST, ExAccount } = require("./config.json");
const URL_prefix = "https://" + HOST + "/BUSINESS";
const AUTH_prefix = "https://" + HOST + "/UAA";
const U = require("./whaleex-sign.js");
async function generateKeys() {
  const keys = U.getKeys();
  //请妥善保管生成后的私钥，不要泄露
  if (keys && keys.publicKey) {
    Print.alreadyGenerate(keys.publicKey);
    return keys;
  } else {
    const privateKey = await ecc.randomKey();
    const publicKey = ecc.privateToPublic(privateKey);
    fs.writeFileSync(
      `ApiKey.json`,
      JSON.stringify({ ...keys, privateKey, publicKey }),
      "utf8"
    );
    Print.successGenerate();
    return { privateKey, publicKey };
  }
}
async function registerPK(access_token) {
  const { publicKey } = U.getKeys();
  var pkBindStatus = await checkPkBindStatus(access_token);
  if (pkBindStatus === "ACTIVED") {
    console.log(`> Local Pk（Actived）： ${publicKey}`);
    return false;
  }
  var path = "/api/account/pk4api";
  var map = {
    pk: publicKey
  };
  var params = U.sort(map);
  return axios({
    method: "post",
    url: URL_prefix + path + "?" + params,
    headers: { Authorization: "bearer " + access_token }
  })
    .then(function(response) {
      Print.pkGenerated(publicKey, response.data.message);
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
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
      Print.loginWelcome(username);
      return response.data;
    })
    .catch(function(error) {
      console.log("login error", error);
      return error;
    });
}
function generateApiKey(access_token) {
  const keys = U.getKeys();
  if (keys && keys.APIKey) {
    Print.alreadyGenerateApiKey();
    return;
  }
  var path = "/api/user/apiKey";
  return axios({
    method: "post",
    url: URL_prefix + path,
    headers: { Authorization: "bearer " + access_token }
  })
    .then(async function(response) {
      Print.successGenerateApiKey(response.data.result);
      fs.writeFileSync(
        `ApiKey.json`,
        JSON.stringify({ ...keys, APIKey: response.data.result }),
        "utf8"
      );
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function checkPkBindStatus(access_token) {
  const { publicKey } = U.getKeys();
  var path = "/api/auth/pk/status";
  var params = U.signData("GET", path, { pk: publicKey });
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params,
    headers: { Authorization: "bearer " + access_token }
  })
    .then(function(response) {
      return response.data.result.status;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}

const createIdStore = async (size = 100) => {
  let ids = [];
  let remark = "0";
  let r = await getIdFromServer(remark, size);
  ids = r.list;
  remark = r.remark;
  return {
    getId: async () => {
      if (ids.length === 0) {
        let { remark: _remark, list: _ids } = await getIdFromServer(
          remark,
          size
        );
        remark = _remark;
        ids = _ids;
      }
      return ids.pop();
    }
  };
};
// const idStore = createIdStore();
// const id = await idStore.getId();

function getIdFromServer(remark, size = 5) {
  //size max 100 !
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
      console.log(error.data);
      return error;
    });
}
function getExecOrder(page = 0, size = 10, symbol) {
  var path = "/api/v1/order/matchresults";
  var params = U.signData("GET", path, { page, size, symbol });
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      return response.data.result.content;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function getOrderBook(symbol = "IQEOS") {
  var path = "/api/public/v1/orderBook/" + symbol;
  return axios({
    method: "get",
    url: URL_prefix + path
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function getTicker(symbol) {
  var path = "/api/public/v1/ticker/" + symbol;
  return axios({
    method: "get",
    url: URL_prefix + path
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function hashPassword(password) {
  const hash = crypto.createHash("sha256");
  hash.update(password);
  return hash.digest("hex");
}
function queryOrderHistory(
  symbol = "IQEOS",
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
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function queryOrderDetail(orderId) {
  var path = "/api/v1/order/orders/" + orderId;
  var params = U.signData("GET", path);
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function queryOpenOrder(side = "buy") {
  var path = "/api/v1/order/openOrders";
  var params = U.signData("GET", path, { side });
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function getHistoryOrder() {
  var path = "/api/v1/order/orders";
  var params = U.signData("GET", path);
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function getOrderDetail(orderId) {
  var path = "/api/v1/order/orders/" + orderId;
  var params = U.signData("GET", path, { orderId });
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      return response.data.result;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function getOpenOrders(page = 0, size = 10) {
  var path = "/api/v1/order/openOrders";
  var params = U.signData("GET", path, { page, size });
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      return response.data.result.content;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function queryMatchResults(
  types = "buy-limit",
  symbol = "IQEOS",
  startDate = "2018-11-07",
  endDate = "2099-11-15",
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
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function queryOneOrderMatchResults(orderId = "87263780807136789") {
  var path = "/api/v1/order/orders/" + orderId + "/matchresults";
  var params = U.signData("GET", path);
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(response.data);
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function queryAssets() {
  var path = "/api/v1/assets";
  var params = U.signData("GET", path, { page: 0, size: 100 });
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      return response.data.result.list.content;
    })
    .catch(function(error) {
      return error.response;
    });
}

function queryOneAsset(currency) {
  var path = "/api/v1/asset/" + currency;
  var params = U.signData("GET", path);
  return axios({
    method: "get",
    url: URL_prefix + path + "?" + params
  })
    .then(function(response) {
      console.log(JSON.stringify(response.data));
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
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
  return axios({
    method: "post",
    url: URL_prefix + path + "?" + params,
    data: order
  })
    .then(function(response) {
      return response.data; //message
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function cancelOrder(orderId) {
  var path = "/api/v1/order/orders/" + orderId + "/submitcancel";
  var params = U.signData("POST", path);
  return axios({
    method: "post",
    url: URL_prefix + path + "?" + params,
    data: {}
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function cancelBatch(ids = ["1", "2", "3"]) {
  var path = "/api/v1/order/orders/batchcancel";
  var params = U.signData("POST", path);
  return axios({
    method: "post",
    url: URL_prefix + path + "?" + params,
    data: {
      "order-ids": ids
    }
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function cancelBatchBySymbol(symbol, side) {
  var path = "/api/v1/order/orders/batchCancelOpenOrders";
  var params = U.signData("POST", path);
  return axios({
    method: "post",
    url: URL_prefix + path + "?" + params + "&symbol=" + symbol,
    data: {}
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function getSymnbol() {
  var path = "/api/public/symbol";
  return axios({
    method: "get",
    url: URL_prefix + path
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}
function getCurrency() {
  var path = "/api/public/currency";
  return axios({
    method: "get",
    url: URL_prefix + path
  })
    .then(function(response) {
      return response.data;
    })
    .catch(function(error) {
      console.log(error.data);
      return error;
    });
}

module.exports = {
  getUserToken, //登录获取token
  generateKeys, //生成私钥和公钥 会保存在keys.json中
  registerPK, //注册公钥
  checkPkBindStatus, //查询pk绑定状态
  generateApiKey, //获取ApiKey 并保存在ApiKey.json中
  getIdFromServer,
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
  cancelBatch,
  getSymnbol,
  getCurrency,
  createIdStore,
  cancelBatchBySymbol
};
