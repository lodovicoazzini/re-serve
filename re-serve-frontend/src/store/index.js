import Vue from 'vue';
import Vuex from 'vuex';
import createPersistedState from 'vuex-persistedstate';

Vue.use(Vuex);

export default new Vuex.Store({
    plugins: [
        createPersistedState({
            storage: window.sessionStorage,
        }),
    ],
    state: {
        user_email: null,
    },
    getters: {
        isAuthenticated(state) {
            return state.user_email != null;
        },
        getUserEmail(state) {
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
