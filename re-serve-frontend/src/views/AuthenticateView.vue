<template>
    <div>
        <v-img contain src="@/assets/logo/logo_transparent_cropped.png"></v-img>
        <validation-observer ref="observer" v-slot="{ invalid }">
            <form @submit.prevent="submit">
                <validation-provider
                    v-slot="{ errors }"
                    name="email"
                    rules="required|email"
                >
                    <v-text-field
                        v-model="email"
                        :error-messages="errors"
                        label="E-mail"
                        required
                    ></v-text-field>
                </validation-provider>

                <v-btn type="submit" :disabled="invalid"> continue </v-btn>
            </form>
        </validation-observer>
    </div>
</template>

<script>
import { required, email } from 'vee-validate/dist/rules';
import {
    extend,
    ValidationObserver,
    ValidationProvider,
    setInteractionMode,
} from 'vee-validate';
setInteractionMode('eager');
extend('required', {
    ...required,
    message: '{_field_} can not be empty',
});
extend('email', {
    ...email,
    message: 'Email must be valid',
});
export default {
    components: {
        ValidationProvider,
        ValidationObserver,
    },
    data: () => ({
        email: '',
    }),
    methods: {
        submit() {
            this.$refs.observer.validate();
            this.$store.commit('authenticate', email);
            this.$router.push({ name: 'my-availabilities' }).catch(() => {});
        },
    },
};
</script>
