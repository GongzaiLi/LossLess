import {shallowMount} from '@vue/test-utils';
import UserSearch from './UserSearch'; // name of your Vue component

let wrapper;
let tableHeaderData = {
  name: 'Someone',
  nickname: 'NickName',
  email: 'Email@Email',
  homeAddress: 'SomeWhere'
};
beforeEach(() => {
  wrapper = shallowMount(UserSearch, {
    propsData: {},
    mocks: {},
    stubs: {},
    methods: {}
  })
});

beforeEach(() => {
  wrapper.destroy();
});

describe('UserSearch', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});
//Boundary getCurrentPageItems()
test('1-response-data-into-table-result-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1).fill(tableHeaderData)))
    .toEqual([tableHeaderData, {name: '-'}, {name: '-'}, {name: '-'}, {name: '-'}]);
});

test('1-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('1-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});
test('1-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('999-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(999).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});
test('999-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(999).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});
test('999-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(999).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('1000-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1000).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('1000-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1000).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});
test('1000-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1000).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('1001-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1001).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('1001-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1001).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('1001-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1001).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

//Blue sky getCurrentPageItems()
test('10-response-data-into-table-result-in-3-perPage', async () => {
  wrapper.vm.perPage = 3;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)))
    .toEqual([tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData,
      tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData, {name: '-'}, {name: '-'}]);
});

test('10-response-data-into-table-length-in-3-perPage', async () => {
  wrapper.vm.perPage = 3;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('10-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});//special case

test('10-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)).length % wrapper.vm.perPage)
    .toEqual(0);
});

//Exception getCurrentPageItems()
test('empty-response-data-into-table-result-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable([]))
    .toEqual([{name: '-'}, {name: '-'}, {name: '-'}, {name: '-'}, {name: '-'}]);
});

test('empty-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable([]).length % wrapper.vm.perPage)
    .toEqual(0);
});

test('empty-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable([]).length)
    .toEqual(1);
});//special case

test('empty-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable([]).length % wrapper.vm.perPage)
    .toEqual(0);
});