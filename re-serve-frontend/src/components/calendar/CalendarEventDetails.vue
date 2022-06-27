<template>
    <v-card>
        <v-card-text>
            <v-container>
                <form>
                    <v-card-title>
                        <span class="text-h5">Event Details</span>
                    </v-card-title>
                    <v-text-field
                        v-model="editedEvent.name"
                        label="Event Name"
                        required
                    ></v-text-field>
                    <v-row class="mt-1 ml-1">
                        <v-btn class="mr-4" @click="closeCallback">
                            cancel
                        </v-btn>
                        <v-btn
                            class="mr-4"
                            @click="saveCallback"
                            color="primary"
                        >
                            save
                        </v-btn>
                        <v-spacer></v-spacer>
                        <v-btn
                            class="mr-4"
                            @click="deleteCallback"
                            color="error"
                        >
                            delete
                        </v-btn>
                    </v-row>
                </form>
            </v-container>
        </v-card-text>
    </v-card>
</template>

<script>
export default {
    data: () => ({
        // editedEvent: this.event,
    }),
    computed: {
        editedEvent() {
            return this.event ? this.event : {};
        },
    },
    props: {
        event: Object,
        saveEvent: Function,
    },
    emits: ['close-dialog'],
    methods: {
        saveCallback() {
            this.saveEvent(this.editedEvent);
            this.closeCallback();
        },
        closeCallback() {
            this.$emit('close-dialog', this.editedEvent);
        },
        deleteCallback() {
            this.closeCallback();
        },
    },
};
</script>
