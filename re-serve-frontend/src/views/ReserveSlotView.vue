<template>
    <v-row class="fill-height">
        <MyCalendar :eventColor="eventColor"></MyCalendar>
        <v-col cols="3">
            <div class="text-center ma-4">
                <v-btn color="primary" block dark @click="backCallback">
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
    props: {
        calendarEmail: {
            type: String,
            required: true,
        },
    },
    computed: {
        userEmail() {
            return this.$store.getters.userEmail;
        },
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
            this.backendLink.get(
                `reservation/create/${event.start}/${event.end}/${event.name}/${this.userEmail}/${this.calendarEmail}`,
                () => this.reloadEvents(),
                (message) => {
                    console.log(message);
                    this.reloadEvents();
                }
            );
        },
        deleteReservation(event) {
            console.log('deleting the reservations');
            this.backendLink.get(
                `reservation/remove/${event.start}/${event.end}/${this.userEmail}`,
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
                `user/listAvailabilities/${this.calendarEmail}`,
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
                `user/listReservations/${this.calendarEmail}/${this.userEmail}`,
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
        backCallback() {
            this.$router.push({ name: 'my-availabilities' }).catch(() => {});
        },
    },
};
</script>
