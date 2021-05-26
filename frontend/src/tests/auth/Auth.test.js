import {getFromLocalStorage, getBusinessActingAs, initializeAuth, getCurrentUser} from '../../auth'
import Api from "../../Api";

beforeEach(() => {
  Object.defineProperty(global, '_localStorage', {
    value: {
      getItem: jest.fn(),
      setItem: jest.fn(),
      clear: jest.fn()
    },
    writable: false,
  });
  Api.getUser = jest.fn();
})

describe('getFromLocalStorage', () => {

  test('works when item exists', () => {
    console.log(localStorage.getItem);
    localStorage.getItem.mockReturnValueOnce("42069");
    expect(getFromLocalStorage('yee')).toBe(42069);
  })

  test('works when item doesn\'t exist', () => {
    console.log(localStorage.getItem);
    localStorage.getItem.mockReturnValueOnce(undefined);
    expect(getFromLocalStorage('yee')).toBe(null);
  })
})

describe('getBusinessActingAs', () => {

  test('works when user admin business', () => {
    expect(getBusinessActingAs({businessesAdministered: [{id:0}, {id: 4}]}, 4)).toStrictEqual({id: 4});
  })

  test('works when user doesn\'t admin business', () => {
    expect(getBusinessActingAs({businessesAdministered: [{id:0}, {id: 4}]}, 9)).toBe(null);
  })

  test('works when user has no business', () => {
    expect(getBusinessActingAs({businessesAdministered: []}, 9)).toBe(null);
  })
})

describe('initializeAuth', () => {

  test('works when no user exists', async () => {
    const userData = {id: 1, name: 'blah', businessesAdministered: [{id: 1}]};

    Api.getUser.mockResolvedValueOnce({data: userData});

    await initializeAuth();

    expect(getCurrentUser()).toBe(null);
  })

  test('works when user exists and not acting as anyone', async () => {
    const userData = {id: 1, name: 'blah', businessesAdministered: [{id: 1}]};
    localStorage.getItem.mockReturnValueOnce("1");
    Api.getUser.mockResolvedValueOnce({data: userData});

    await initializeAuth();

    expect(getCurrentUser()).toStrictEqual({id: 1, name: 'blah', businessesAdministered: [{id: 1}], currentlyActingAs: null});
  })

  // test('works when user exists but API fails', async () => {
  //   localStorage.getItem.mockReturnValueOnce("1");
  //   Api.getUser.mockRejectedValueOnce(new Error());
  //
  //   await initializeAuth();
  //
  //   expect(getCurrentUser()).toBe(null);
  // })

  test('works when user exists and acting as a business', async () => {
    const userData = {id: 1, name: 'blah', businessesAdministered: [{id: 2}]};
    localStorage.getItem.mockReturnValueOnce("1").mockReturnValueOnce("2");
    Api.getUser.mockResolvedValueOnce({data: userData});

    await initializeAuth();

    expect(getCurrentUser()).toStrictEqual({id: 1, name: 'blah', businessesAdministered: [{id: 2}], currentlyActingAs: {id: 2}});
  })

})