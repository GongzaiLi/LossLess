import CreateBusiness from "@/components/business/CreateBusiness.vue";
import {shallowMount} from "@vue/test-utils";

describe ("createBusiness.vue", ()=> {
    let wrapper;
    beforeEach(()=> {
        wrapper = shallowMount(CreateBusiness, {
            methods: {toggle: ()=> {}}
        })
        })
    it("renders", () => {
        expect(wrapper.exists()).toBe(true);
    })
    it("does h1 exist", ()=>{
        expect(wrapper.find("h1").text()).toBe("Create a Business");
    })
})