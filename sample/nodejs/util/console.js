const { HOST, ExAccount, userEosAccount } = require("../config.json");
function needUserInfo() {
  console.log(
    "> Please perfect personal information in config.json：【 user, location, userEosAccount 】"
  );
}
function loginWelcome(username) {
  let _username = username.split("");
  _username.splice(3, 4, "****");
  console.log("> WhaleEx: login success.");
}
function pkGenerated(publicKey, msg) {
  console.log(msg);
  console.log("|");
  console.log(
    "| Please transfer any amount of EOS to the below contract address with your bound EOS account."
  ); //本地公钥未激活, 通过小额转账激活
  console.log("| Contract Account:", ExAccount); //收款合约账户
  console.log("| memo:", "bind:" + publicKey + ":WhaleEx");
}
function checkPkStatus(callBack) {
  process.stdout.write("I have transfered，query pk bind status（y/n）:");
  process.stdin.on("data", input => {
    input = input.toString().trim();
    if (["Y", "y", "YES", "yes"].indexOf(input) > -1) {
      console.log("> STORED (not actived) | ACTIVED ");
      callBack && callBack();
    }
    if (["N", "n", "NO", "no"].indexOf(input) > -1) process.exit(0);
  });
}
function showTable(index, list, keys) {
  let items = {};
  function Instance(obj) {
    keys.forEach(key => {
      this[key] = obj[key];
    });
  }
  list.forEach(i => {
    items[i[index]] = new Instance(i);
  });
  console.table(items);
}
function insertPassword(user) {
  return new Promise(reslove => {
    let _username = user.split("");
    _username.splice(3, 4, "****");
    process.stdout.write(
      `> Welcome ${_username.join("")}, please insert password:`
    );
    process.stdin.on("data", input => {
      input = input.toString().trim();
      reslove(input);
    });
  });
}
function showSymbolList(list) {
  console.log("Pairs：");
  showTable("name", list, [
    "id",
    "baseCurrency",
    "quoteCurrency",
    "basePrecision",
    "quotePrecision",
    "lastPrice"
  ]);
}
function showCurrencyList(list) {
  console.log("Coin List：");
  showTable("shortName", list, ["depositAddress", "contract"]);
}
function alreadyGenerate(publicKey) {
  console.log(
    `> WhaleEx: privateKey, publicKey already exist. (delete privateKey and publicKey in ApiKey.json to regenerate)`
  );
  console.log(`> ---- memo: bind:${publicKey}:WhaleEx`);
}
function successGenerate() {
  console.log(
    `> WhaleEx: privateKey, publicKey generate success. (Please store your private key after encryption!)`
  );
}
function alreadyGenerateApiKey() {
  console.log(
    "> WhaleEx: APIKey already exist. (delete APIKey in ApiKey.json to regenerate)"
  );
}
function successGenerateApiKey(apikey) {
  console.log("> WhaleEx: APIKey generate success. APIKey: " + apikey);
}
function consoleSymbol(symbol) {
  console.log("Pair Info：");
  showTable(
    "name",
    [symbol],
    ["baseCurrency", "quoteCurrency", "basePrecision", "quotePrecision"]
  );
}
function consoleOrderBook(orderBook, symbol) {
  console.log("Order Book：");
  const { asks, bids } = orderBook;
  let _asks = asks.reverse().map((i, idx) => {
    return { ...i, index: `Sell${asks.length - idx}` };
  });
  let _bids = bids.map((i, idx) => {
    return { ...i, index: `Buy${idx + 1}` };
  });
  showTable(
    "index",
    [
      ..._asks,
      { index: "----", price: symbol.lastPrice, quantity: symbol.name },
      ..._bids
    ],
    ["price", "quantity"]
  );
}
function consoleMarket(symbolMarket, symbol) {
  console.log(symbol.name + "Pair Market：");
  showTable(
    "name",
    [{ ...symbol, ...symbolMarket }],
    ["symbolId", "priceChangePercent", "lastPrice", "baseVolume"]
  );
  showTable("name", [{ ...symbol, ...symbolMarket }], ["open", "high", "low"]);
}
function consoleAsset(assetList) {
  console.log("Your Assets：");
  function Asset(totalAmount, fixedAmount, availableAmount, privatePlacement) {
    this.totalAmount = totalAmount;
    this.fixedAmount = fixedAmount;
    this.availableAmount = availableAmount;
    this.privatePlacement = privatePlacement;
  }
  var assets = {};
  assetList.forEach(i => {
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
}
function consoleOrders(orderList) {
  console.log("Current Open Order：size=5");
  function Order(symbolId, price, origQty, type, side) {
    this.symbolId = symbolId;
    this.price = price;
    this.origQty = origQty;
    this.type = type;
    this.side = side;
  }
  var orders = {};
  orderList.forEach(i => {
    const { orderId, symbolId, price, origQty, type, side } = i;
    orders[orderId] = new Order(symbolId, price, origQty, type, side);
  });
  console.table(orders);
}
function consoleOrderDetail(orderList) {
  if (orderList.length > 0 && orderList[0]) {
    const { orderId } = orderList[0];
    console.log(`Open Order Detail：${orderId}`);
    showTable("orderId", orderList, [
      "symbolId",
      "price",
      "origQty",
      "execQty",
      "type",
      "side",
      "status"
    ]);
  }
}
function consoleExecOrders(list, symbol) {
  console.log(symbol.name + "Transaction List：");
  const _list = list.map(i => ({
    ...i,
    typeStr: (i.type === 76 && "limit") || "market",
    sideStr: (i.side === 83 && "sell") || "buy"
  }));
  showTable("orderId", _list, [
    "price",
    "quantity",
    "volume",
    "typeStr",
    "sideStr"
  ]);
}
function consolePlaceOrder(orderList, orderResult, orderIdList) {
  console.log("Current Order Detail：");
  const list = orderList.map((i, idx) => {
    return { ...i, ...orderResult[idx], orderId: orderIdList[idx] };
  });
  showTable("orderId", list, ["symbol", "price", "amount", "type", "message"]);
}
function consoleCancelOrder(r, orderId) {
  if (r.returnCode === "0") {
    console.log(orderId + " Cancel withdrawal Success!");
  } else {
    console.log(orderId + " Cancel withdrawal Fail!" + r.message);
  }
}
function consoleCancelBatch(r) {
  if (r.returnCode === "0") {
    console.log(" Batch Cancel withdrawal Success!");
  } else {
    console.log(" Batch Cancel withdrawal Fail!" + r.message);
  }
}
module.exports = {
  needUserInfo,
  loginWelcome,
  pkGenerated,
  checkPkStatus,
  showSymbolList,
  showCurrencyList,
  insertPassword,
  alreadyGenerate,
  successGenerate,
  alreadyGenerateApiKey,
  successGenerateApiKey,
  consoleSymbol,
  consoleOrderBook,
  consoleMarket,
  consoleAsset,
  consoleOrders,
  consoleExecOrders,
  consolePlaceOrder,
  consoleOrderDetail,
  consoleCancelOrder,
  consoleCancelBatch
};
