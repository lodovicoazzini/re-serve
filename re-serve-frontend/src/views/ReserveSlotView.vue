<template>
    <v-row class="fill-height">
        <MyCalendar :eventColor="eventColor"></MyCalendar>
        <v-col cols="3">
            <div class="text-center ma-4">
                <v-btn color="error" block dark @click="backCallback">
                    back
                </v-btn>
            </div>
        </v-col>
    </v-row>
</template>

<script>
import MyCalendar from '@/components/calendar/MyCalendar.vue';

export default {
    components: {
        MyCalendar,
    },
    data() {
        return {
            reserveDialog: false,
            events: [],
            eventColor: 'orange',
        };
    },
    provide() {
        return {
            getEvents: () => this.events,
            saveEvent: this.saveReservation,
            deleteEvent: this.deleteReservation,
        };
    },
    mounted() {
        this.reloadEvents();
    },
    methods: {
        saveReservation(event) {
            console.log(this.$store.getters.getUserEmail);
            this.backendLink.get(
                `reservation/create/${event.start}/${event.end}/${event.name}/email`,
                () => this.reloadEvents(),
                (message) => {
                    console.log(message);
                    this.reloadEvents();
                }
            );
        },
        deleteReservation(event) {
            this.backendLink.get(
                `reservation/remove/${event.start}/${event.end}/'email'`,
                () => this.reloadEvents(),
                (message) => {
                    console.log(message);
                    this.reloadEvents();
                }
            );
        },
        reloadEvents() {
            this.events = [];
            this.backendLink.get(
                `availability/list`,
                (response) => {
                    const mapped = response.data.map((availability) => ({
                        start: availability.startTime,
                        end: availability.endTime,
                        name: null,
                        color: 'grey',
                        timed: true,
                        editable: false,
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => console.log(message)
            );
            this.backendLink.get(
                `reservation/list`,
                (response) => {
                    const mapped = response.data.map((availability) => ({
                        start: availability.startTime,
                        end: availability.endTime,
                        name: availability.title,
                        color: this.eventColor,
                        timed: true,
                        editable: true,
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => console.log(message)
            );
        },
    },
};
</script>
