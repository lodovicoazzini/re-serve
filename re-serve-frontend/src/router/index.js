import Vue from 'vue';
import VueRouter from 'vue-router';
import MyAvailabilitiesView from '@/views/MyAvailabilitiesView';
import AuthenticateView from '@/views/AuthenticateView';
import ReserveSlotView from '@/views/ReserveSlotView';

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        name: 'my-availabilities',
        component: MyAvailabilitiesView,
    },
    {
        path: '/authenticate',
        name: 'authenticate',
        component: AuthenticateView,
    },
    {
        path: '/reserve-slot/:calendarEmail',
        name: 'reserve-slot',
        component: ReserveSlotView,
        props: true,
    },
    {
        path: '/about',
        name: 'about',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import('@/views/AboutView.vue'),
    },
];

const router = new VueRouter({
    routes,
});

export default router;
