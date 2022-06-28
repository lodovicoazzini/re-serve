<template>
    <v-app id="app">
        <v-app-bar app>
            <v-img
                class="mx-2"
                src="@/assets/logo/logo_transparent_cropped.png"
                max-height="40"
                max-width="200"
                contain
                @click="homeCallback"
            ></v-img>
            <v-spacer></v-spacer>
            <v-btn
                v-if="$store.getters.isAuthenticated"
                color="error"
                @click="backCallback"
            >
                logout
            </v-btn>
            <v-btn class="ml-5" icon color="black" @click="callbackAbout">
                <v-icon>mdi-information-outline</v-icon>
            </v-btn>
        </v-app-bar>

        <v-main>
            <v-row class="ma-0 py-4 ps-8" justify="center">
                <router-view />
            </v-row>
        </v-main>

        <v-footer padless>
            <v-col class="text-center" cols="12">
                {{ new Date().getFullYear() }} —
                <strong>With </strong>
                <v-icon color="red">mdi-heart</v-icon>
                <strong> by Lodovico Azzini</strong>
            </v-col>
        </v-footer>
    </v-app>
</template>

<script>
export default {
    name: 'App',
    data: () => ({}),
    methods: {
        backCallback() {
            this.$store.commit('authenticate', null);
            this.$router.push({ name: 'authenticate' }).catch(() => {});
        },
        callbackAbout() {
            this.$router.push({ name: 'about' }).catch(() => {});
        },
        homeCallback() {
            const destination = this.$store.getters.isAuthenticated
                ? 'my-availabilities'
                : 'authenticate';
            this.$router.push({ name: destination }).catch(() => {});
        },
    },
};
</script>
