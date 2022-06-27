<template>
    <v-row class="fill-height">
        <MyCalendar></MyCalendar>
        <v-col cols="3">
            <div class="text-center ma-4">
                <ReserveDialog></ReserveDialog>
            </div>
        </v-col>
    </v-row>
</template>

<script>
import ReserveDialog from '@/components/ReserveDialog.vue';
import MyCalendar from '@/components/calendar/MyCalendar.vue';

export default {
    components: {
        ReserveDialog,
        MyCalendar,
    },
    data: () => ({
        reserveDialog: false,
        availabilities: [],
    }),
    provide() {
        return {
            events: this.availabilities,
            saveEvent: this.saveEvent,
            deleteEvent: this.deleteEvent,
        };
    },
    methods: {
        saveEvent(event) {
            console.log('saving' + event.name);
        },
        deleteEvent(event) {
            this.availabilities = this.availabilities.filter(
                (availability) => availability.name != event.name
            );
            console.log('deleting' + event.name);
        },
    },
};
</script>
