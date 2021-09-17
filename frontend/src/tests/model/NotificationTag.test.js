import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import NotificationTag from "../../components/model/NotificationTag";

let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings

let tagColor;

let $route;
let $router;

jest.mock('../../Api');

const $log = {
    debug: jest.fn(),
};

beforeEach(() => {
    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    tagColor = "None"
    $route = {
        name: "users",
        params: {
            id: 0
        },
        query: {}
    };

    $router = {
        push: jest.fn(),
        replace: jest.fn(),
        go: jest.fn(),
    }

    wrapper = shallowMount(NotificationTag, {
        localVue,
        propsData: {tagColor},
        mocks: {$log, $router, $route},
        stubs: {},
        methods: {},
    });
});


afterEach(() => {
    wrapper.destroy();
});

describe('Check if determineTagCSSColor sets correct color for tag', () => {
    test('tag color value is red', async () => {
        await wrapper.setProps({ tagColor: 'RED' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("hsla(360, 100%, 53%, 1)");
    });
    test('tag color value is orange', async () => {
        await wrapper.setProps({ tagColor: 'ORANGE' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("hsla(39, 100%, 50%, 1)");
    });
    test('tag color value is yellow', async () => {
        await wrapper.setProps({ tagColor: 'YELLOW' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("hsla(51, 100%, 50%, 1)");
    });
    test('tag color value is green', async () => {
        await wrapper.setProps({ tagColor: 'GREEN' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("hsla(101, 100%, 50%, 1)");
    });
    test('tag color value is blue', async () => {
        await wrapper.setProps({ tagColor: 'BLUE' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("hsla(191, 100%, 50%, 1)");
    });
    test('tag color value is purple', async () => {
        await wrapper.setProps({ tagColor: 'PURPLE' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("hsla(290, 100%, 50%, 1)");
    });
    test('tag color value is black', async () => {
        await wrapper.setProps({ tagColor: 'BLACK' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("hsla(1, 100%, 0%, 1)");
    });
    test('tag color value is other', async () => {
        await wrapper.setProps({ tagColor: 'ThisStringDoesntMatter' })
        await wrapper.vm.determineTagCSSColor();
        expect(wrapper.vm.tagStyle.background).toStrictEqual("linear-gradient(80deg, #ffa500 50%, #292929 50%)");
    });
});
