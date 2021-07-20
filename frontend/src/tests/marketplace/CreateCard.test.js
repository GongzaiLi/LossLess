import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import CreateCard from '../../components/marketplace/CreateCard';


config.showDeprecationWarnings = false  //to disable deprecation warnings


let wrapper;

const $route = {
    params: {
        id: 0
    }
};
const $log = {
    debug() {
    }
};

jest.mock('../../Api');

beforeEach(() => {

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(CreateCard, {
        localVue,
        propsData: {
        },
        mocks: {$route, $log},
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('testing create listing', () => {

    beforeEach(()=> {
    })

})
