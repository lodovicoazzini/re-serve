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
            <v-snackbar v-model="snackbar" :timeout="timeout" :color="color">
                {{ text }}

                <template v-slot:action="{ attrs }">
                    <v-btn
                        color="white"
                        text
                        v-bind="attrs"
                        @click="snackbar = false"
                    >
                        Close
                    </v-btn>
                </template>
            </v-snackbar>
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
    data: () => ({
        snackbar: false,
        text: '',
        color: null,
        timeout: 2000,
    }),
    provide() {
        return {
            notify: (message, color) => this.notify(message, color),
        };
    },
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
        notify(message, color) {
            this.text = message;
            this.snackbar = true;
            this.color = color;
        },
    },
};
</script>
