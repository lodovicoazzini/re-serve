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
        user: {
            email: null,
        },
    },
    getters: {
        isAuthenticated(state) {
            return state.user.email != null;
        },
        getUserEmail(state) {
            return state.user.email;
        },
    },
    mutations: {
        authenticate(state, email) {
            state.user.email = email;
        },
    },
    actions: {},
    modules: {},
});
