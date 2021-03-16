import {createLocalVue, shallowMount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import UserSearch from '../UserSearch'; // name of your Vue component

let wrapper;
let tableHeaderData = {
  name: 'Someone',
  nickname: 'NickName',
  email: 'Email@Email',
  homeAddress: 'SomeWhere'
};

beforeEach(() => {
  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = shallowMount(UserSearch, {
    localVue,
    propsData: {},
    mocks: {},
    stubs: {},
    methods: {},
    computed: {}
  })
});

afterEach(() => {
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
    .toEqual([tableHeaderData]);
});

test('1-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1).fill(tableHeaderData)).length)
    .toEqual(1);
});

test('1-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1).fill(tableHeaderData)).length)
    .toEqual(1);
});
test('1-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1).fill(tableHeaderData)).length)
    .toEqual(1);
});

test('999-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(999).fill(tableHeaderData)).length)
    .toEqual(999);
});
test('999-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(999).fill(tableHeaderData)).length)
    .toEqual(999);
});
test('999-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(999).fill(tableHeaderData)).length)
    .toEqual(999);
});

test('1000-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1000).fill(tableHeaderData)).length)
    .toEqual(1000);
});

test('1000-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1000).fill(tableHeaderData)).length)
    .toEqual(1000);
});
test('1000-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1000).fill(tableHeaderData)).length)
    .toEqual(1000);
});

test('1001-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1001).fill(tableHeaderData)).length)
    .toEqual(1001);
});

test('1001-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1001).fill(tableHeaderData)).length)
    .toEqual(1001);
});

test('1001-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(1001).fill(tableHeaderData)).length)
    .toEqual(1001);
});

//Blue sky getCurrentPageItems()
test('10-response-data-into-table-result-in-3-perPage', async () => {
  wrapper.vm.perPage = 3;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)))
    .toEqual([tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData,
      tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData, tableHeaderData]);
});

test('10-response-data-into-table-length-in-3-perPage', async () => {
  wrapper.vm.perPage = 3;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)).length)
    .toEqual(10);
});

test('10-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)).length)
    .toEqual(10);
});//special case

test('10-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable(Array(10).fill(tableHeaderData)).length)
    .toEqual(10);
});

//Exception getCurrentPageItems()
test('empty-response-data-into-table-result-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable([]))
    .toEqual([]);
});

test('empty-response-data-into-table-length-in-5-perPage', async () => {
  wrapper.vm.perPage = 5;
  expect(wrapper.vm.getUserInfoIntoTable([]).length)
    .toEqual(0);
});

test('empty-response-data-into-table-length-in-1-perPage', async () => {
  wrapper.vm.perPage = 1;
  expect(wrapper.vm.getUserInfoIntoTable([]).length)
    .toEqual(0);
});//special case

test('empty-response-data-into-table-length-in-1000-perPage', async () => {
  wrapper.vm.perPage = 1000;
  expect(wrapper.vm.getUserInfoIntoTable([]).length)
    .toEqual(0);
})


//Boundary itemsRangeMin()
test('1_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

test('1000_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

test('1_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

test('1000_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

//Blue sky itemsRangeMin()
test('50_total_result-in-10_perPage-in-2_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(11);
})

test('50_total_result-in-10_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(11);
})

test('100_total_result-in-20_perPage-in-5_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 100;
  wrapper.vm.perPage = 20;
  wrapper.vm.currentPage = 5;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(81);
})
//Exception itemsRangeMin()
test('0_total_result-in-0_perPage-in-0_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 0;
  wrapper.vm.perPage = 0;
  wrapper.vm.currentPage = 0;
  expect(parseInt(wrapper.vm.itemsRangeMin))
    .toEqual(0);
})



//Boundary itemsRangeMax()
test('1_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1);
})

test('1000_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1);
})

test('1_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1);
})

test('1000_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1000);
})

//Blue sky itemsRangeMax()
test('50_total_result-in-10_perPage-in-2_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(20);
})

test('50_total_result-in-10_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(20);
})

test('100_total_result-in-20_perPage-in-5_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 100;
  wrapper.vm.perPage = 20;
  wrapper.vm.currentPage = 5;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(80);
})
//Exception itemsRangeMax()
test('0_total_result-in-0_perPage-in-0_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 0;
  wrapper.vm.perPage = 0;
  wrapper.vm.currentPage = 0;
  expect(parseInt(wrapper.vm.itemsRangeMax))
    .toEqual(0);
})