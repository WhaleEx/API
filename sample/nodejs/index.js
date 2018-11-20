/**
generateKeys, //生成私钥和公钥 会保存在keys.json中
registerPK, //注册公钥
checkPkBindStatus, //查询pk绑定状态
getApiKey, //获取ApiKey 并保存在ApiKey.json中
getIdFromServer,//获取globalIds
placeOrder,//下单
getOpenOrders,//获取当前订单
cancelOrder,//撤单
getOrderDetail,//获取某一订单详情
getHistoryOrder,//历史委托
getExecOrder,//查询成交
queryOneOrderMatchResults,//查询单一委托成交
queryAssets,//获取用户资产
queryOneAsset,//获取用户某一资产
getTicker//获取最新行情
getOrderBook//获取订单铺
 */
const WAL = require("./whaleex-api.js");
const Print = require("./util/console.js");
const Config = require("./config.json");
const U = require("./whaleex-sign.js");
const fs = require("fs-extra");
console.log("welcome WhaleEx API");
generateKeys = () => {
  WAL.generateKeys();
};
loopPkBindStatus = async (access_token, _reslove) => {
  let pkBindStatus;
  process.stdout.write(
    "checking pk bind status. after transfer, pk will be actived in 3-5 minutes"
  );
  while (pkBindStatus !== "ACTIVED") {
    process.stdout.write(".");
    pkBindStatus = await WAL.checkPkBindStatus(access_token);
    await new Promise(reslove => {
      setTimeout(reslove, 2000);
    });
  }
  if (pkBindStatus === "ACTIVED") {
    console.log("publicKey is Actived！");
    const keys = U.getKeys() || {};
    fs.writeFileSync(
      `ApiKey.json`,
      JSON.stringify({ ...keys, isPkActived: true }),
      "utf8"
    );
    _reslove && _reslove();
  }
};
async function generateSignKeys() {
  const { user, location, userEosAccount } = Config;
  const keys = U.getKeys() || {};
  if (!keys.APIKey || !keys.privateKey) {
    //step-2 用户登录
    let password = await Print.insertPassword(user);
    const token = await WAL.getUserToken(user, password, location);
    //step-3 获取APIKey
    await WAL.generateApiKey(token.access_token);
    //step-4 生成公钥，私钥
    await WAL.generateKeys();
    const pkR = await WAL.registerPK(token.access_token);
    if (!!pkR) {
      return new Promise(reslove => {
        Print.checkPkStatus(() => {
          loopPkBindStatus(token.access_token, reslove);
        });
      });
    }
  } else if (!keys.isPkActived) {
    Print.alreadyGenerateApiKey();
    Print.alreadyGenerate(keys.publicKey);
    let password = await Print.insertPassword(user);
    const token = await WAL.getUserToken(user, password, location);
    return new Promise(reslove => {
      Print.checkPkStatus(() => {
        loopPkBindStatus(token.access_token, reslove);
      });
    });
  }
}
createSymbolObj = (symbol, currencyList) => {
  const { baseCurrency, quoteCurrency, basePrecision, quotePrecision } = symbol;
  const B = currencyList.filter(
    ({ shortName }) => shortName === baseCurrency
  )[0];
  const Q = currencyList.filter(
    ({ shortName }) => shortName === quoteCurrency
  )[0];
  return {
    baseCurrency,
    quoteCurrency,
    basePrecision,
    quotePrecision,
    baseContract: B.contract,
    quoteContract: Q.contract
  };
};
createOrder = (amount, price, symbol, type) => {
  return { amount, price, symbol, type };
};
runTrade = async function() {
  //step-1 查询交易对和币种
  let symbolList = await WAL.getSymnbol();
  let currencyList = await WAL.getCurrency();
  Print.showSymbolList(symbolList);
  Print.showCurrencyList(currencyList);
  //step-2 完善用户信息
  const { user, location, userEosAccount } = Config;
  if (!user || !location || !userEosAccount) {
    Print.needUserInfo();
    return;
  }
  //step-3 获取APIKey
  //step-4 生成公钥，私钥
  await generateSignKeys();
  //step-5 打印待下单交易对信息
  const symbol = symbolList[3];
  Print.consoleSymbol(symbol);
  const orderBook = await WAL.getOrderBook(symbol.name);
  Print.consoleOrderBook(orderBook, symbol);
  const symbolMarket = await WAL.getTicker(symbol.name);
  Print.consoleMarket(symbolMarket, symbol);
  //step-6 打印用户资产
  const assets = await WAL.queryAssets();
  Print.consoleAsset(assets);
  //step-7 取当前委托
  const orders = await WAL.getOpenOrders(0, 5);
  Print.consoleOrders(orders);
  //setp-8 获取历史成交
  const execOrders = await WAL.getExecOrder(0, 5, symbol.name);
  Print.consoleExecOrders(execOrders, symbol);
  //step-9 获取用户可用资产
  const _assetList = assets.filter(
    ({ availableAmount }) => +availableAmount > 0
  );
  if (_assetList.length === 0) {
    process.exit(0);
    return;
  }
  //step-10 下单
  const symbolObj = createSymbolObj(symbol, currencyList);
  const idStore = await WAL.createIdStore(5);
  let orderList = [
    createOrder("0.001", "99999", symbol.name, "sell-limit"),
    createOrder("1", "0.0001", symbol.name, "buy-limit"),
    createOrder("999999", "99999", symbol.name, "sell-limit")
  ];
  let list = [];
  const orderResult = await Promise.all(
    orderList.map(async (order, idx) => {
      const orderId = await idStore.getId();
      list.push(orderId);
      return WAL.placeOrder(orderId, userEosAccount, order, symbolObj); //下单
    })
  );
  Print.consolePlaceOrder(orderList, orderResult, list);
  //step-11 取委托明细
  let orderId = list[0];
  const orderDetail = await WAL.getOrderDetail(orderId);
  Print.consoleOrderDetail([orderDetail]);
  //step-12 撤单
  const cancelOrder = await WAL.cancelOrder(orderId);
  Print.consoleCancelOrder(cancelOrder, orderId);
  //step-13 批量撤单
  const r = await WAL.cancelBatchBySymbol();
  Print.consoleCancelBatch(r);
};
runTrade(); //交易简单流程
