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
    data() {
        return {
            reserveDialog: false,
            availabilities: [],
        };
    },
    provide() {
        return {
            getEvents: () => this.availabilities,
            events: this.availabilities,
            saveEvent: this.saveEvent,
            deleteEvent: this.deleteEvent,
        };
    },
    mounted() {
        this.reloadAvailabilities();
    },
    methods: {
        saveEvent(event) {
            this.backendLink.get(
                `availability/create/${event.start}/${event.end}`,
                () => this.reloadAvailabilities(),
                (message) => {
                    console.log(message);
                    this.reloadAvailabilities();
                }
            );
        },
        deleteEvent(event) {
            this.backendLink.get(
                `availability/remove/${event.start}/${event.end}`,
                () => this.reloadAvailabilities(),
                (message) => {
                    console.log(message);
                    this.reloadAvailabilities();
                }
            );
        },
        reloadAvailabilities() {
            this.backendLink.get(
                `availability/list`,
                (response) => {
                    const mapped = response.data.map((availability) => ({
                        start: availability.startTime,
                        end: availability.endTime,
                        name: null,
                        color: 'green',
                        timed: true,
                    }));
                    this.availabilities = mapped;
                },
                (message) => console.log(message)
            );
        },
    },
};
</script>
