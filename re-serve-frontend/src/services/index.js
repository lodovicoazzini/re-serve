import Vue from 'vue';

import backendLink from './backendLink';

export default {
    install: () => {
        Vue.prototype.backendLink = backendLink;
        Vue.backendLink = backendLink;
    },
};
