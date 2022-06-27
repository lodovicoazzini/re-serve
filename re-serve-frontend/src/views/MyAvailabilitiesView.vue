<template>
    <v-row class="fill-height">
        <MyCalendar
            :loadEvents="loadEvents"
            :saveEvent="saveEvent"
        ></MyCalendar>
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
    }),
    methods: {
        loadEvents() {
            return [];
        },
        saveEvent(event) {
            console.log(event);
        },
    },
};
</script>
