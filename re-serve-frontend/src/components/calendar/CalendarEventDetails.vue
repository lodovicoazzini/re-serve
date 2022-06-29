<template>
    <v-card>
        <v-card-text>
            <v-container>
                <form>
                    <v-card-title>
                        <span class="text-h5">Event Details</span>
                    </v-card-title>
                    <v-text-field
                        v-model="safeEvent.name"
                        label="Event Name"
                        required
                    ></v-text-field>
                    <v-row class="mt-1 ml-1">
                        <v-btn class="mr-4" @click="closeDialog">
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
    props: {
        selectedEvent: Object,
    },
    computed: {
        safeEvent() {
            return this.selectedEvent ? this.selectedEvent : {};
        },
    },
    inject: ['saveEvent', 'deleteEvent', 'updateTitle'],
    emit: ['close-dialog'],
    methods: {
        closeDialog() {
            this.$emit('close-dialog');
        },
        saveCallback() {
            this.updateTitle(this.safeEvent);
            this.closeDialog();
        },
        deleteCallback() {
            this.deleteEvent(this.safeEvent);
            this.closeDialog();
        },
    },
};
</script>
