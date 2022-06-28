import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        user_email: null,
    },
    getters: {
        isAuthenticated(state) {
            return state.user_email != null;
        },
        userEmail(state) {
            return state.user_email;
        },
    },
    mutations: {
        authenticate(state, email) {
            state.user_email = email;
        },
    },
    actions: {},
    modules: {},
});
