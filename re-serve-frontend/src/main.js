import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';
import services from './services';
import Embed from 'v-video-embed';

import 'material-design-icons-iconfont/dist/material-design-icons.css';
import '@mdi/font/css/materialdesignicons.css';

Vue.use(services);
Vue.use(Embed);

Vue.config.productionTip = false;

new Vue({
    router,
    store,
    vuetify,
    render: (h) => h(App),
    beforeCreate() {
        if (!this.$store.getters.isAuthenticated) {
            this.$router.push({ name: 'authenticate' }).catch(() => {});
        }
    },
}).$mount('#app');
