import Api from "../../Api";


describe("Get user's country", () => {
  let returnedData;

  beforeAll(() => {
    global.fetch = jest.fn().mockImplementation(
      async () => ({ok: true, json: async () => returnedData})
    );
  })

  afterAll(() => {
    global.fetch.mockClear();
    delete global.fetch;
  })

  test('when exists a currency', async () => {
    returnedData = [{"currencies":[{"code":"EUR","name":"Euro","symbol":"€"}]}];

    const currency = await Api.getUserCurrency('New Zealand');
    expect(window.fetch).toHaveBeenCalledWith('https://restcountries.eu/rest/v2/name/New%20Zealand?fields=currencies');
    expect(currency).toEqual({"code":"EUR","name":"Euro","symbol":"€"});
  });

  test('when no countries exist', async () => {
    returnedData = {"status":404,"message":"Not Found"};

    const currency = await Api.getUserCurrency('');
    expect(currency).toEqual(null);
  });

  test('when no currencies exist exist', async () => {
    returnedData = [{"currencies":[]}];

    const currency = await Api.getUserCurrency('');
    expect(currency).toEqual(null);
  });

  test('when many currencies', async () => {
    returnedData = [{"currencies":[{"code":"BWP","name":"Botswana pula","symbol":"P"},{"code":"GBP","name":"British pound","symbol":"£"},{"code":"CNY","name":"Chinese yuan","symbol":"¥"},{"code":"EUR","name":"Euro","symbol":"€"},{"code":"INR","name":"Indian rupee","symbol":"₹"},{"code":"JPY","name":"Japanese yen","symbol":"¥"},{"code":"ZAR","name":"South African rand","symbol":"Rs"},{"code":"USD","name":"United States dollar","symbol":"$"},{"code":"(none)","name":null,"symbol":null}]}];

    const currency = await Api.getUserCurrency('zimbabwe');
    expect(currency).toEqual({"code":"BWP","name":"Botswana pula","symbol":"P"});
  });

  test('when garbage data', async () => {
    returnedData = [{"currencies":[{"code":"(none)","name":null,"symbol":null}]}];

    const currency = await Api.getUserCurrency('zimbabwe');
    expect(currency).toEqual(null);
  });
})