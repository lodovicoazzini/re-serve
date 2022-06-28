<template>
    <v-row class="fill-height">
        <MyCalendar :eventColor="eventColor"></MyCalendar>
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
            eventColor: 'green',
        };
    },
    provide() {
        return {
            getEvents: () => this.availabilities,
            saveEvent: this.saveAvailability,
            deleteEvent: this.deleteAvailability,
        };
    },
    mounted() {
        this.reloadAvailabilities();
    },
    methods: {
        saveAvailability(event) {
            this.backendLink.get(
                `availability/create/${event.start}/${event.end}`,
                () => this.reloadAvailabilities(),
                (message) => {
                    console.log(message);
                    this.reloadAvailabilities();
                }
            );
        },
        deleteAvailability(event) {
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
                        color: this.eventColor,
                        timed: true,
                        editable: true,
                    }));
                    this.availabilities = mapped;
                },
                (message) => console.log(message)
            );
        },
    },
};
</script>
