<template>
    <v-row justify="center">
        <v-dialog v-model="dialog" persistent max-width="600px">
            <template v-slot:activator="{ on, attrs }">
                <v-btn color="primary" block dark v-bind="attrs" v-on="on">
                    reserve slot
                </v-btn>
            </template>
            <v-card>
                <v-card-title>
                    <span class="text-h5">Other User Email</span>
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-row>
                            <v-col cols="12">
                                <v-text-field
                                    label="Email*"
                                    required
                                    v-model="email"
                                ></v-text-field>
                            </v-col>
                        </v-row>
                    </v-container>
                    <small>*indicates required field</small>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="error" text @click="dialog = false">
                        close
                    </v-btn>
                    <v-btn
                        color="primary"
                        text
                        @click="handleNext"
                        :disabled="email == ''"
                    >
                        next
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script>
export default {
    data: () => ({
        dialog: false,
        email: '',
    }),
    methods: {
        handleNext() {
            if (this.email != '') {
                this.dialog = false;
                this.$router.push({ name: 'reserve-slot' }).catch(() => {});
            }
        },
    },
};
</script>
