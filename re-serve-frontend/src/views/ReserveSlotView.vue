<template>
    <v-col>
        <v-row>
            <v-span>
                Click and drag on the calendar to reserve a slot. <br />
                Make sure to select an available time slot (i.e., colored in
                grey).
            </v-span>
        </v-row>
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
    </v-col>
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
            updateTitle: this.updateReservationTitle,
        };
    },
    inject: ['notify'],
    mounted() {
        this.reloadEvents();
    },
    methods: {
        saveReservation(event) {
            this.backendLink.get(
                `reservation/create/${event.start}/${event.end}/${event.name}/${this.userEmail}/${this.calendarEmail}`,
                () => this.reloadEvents(),
                (message) => {
                    this.notify(message, 'error');
                    this.reloadEvents();
                }
            );
        },
        deleteReservation(event) {
            this.backendLink.get(
                `reservation/remove/${event.start}/${event.end}/${this.userEmail}`,
                () => this.reloadEvents(),
                (message) => {
                    this.notify(message, 'error');
                    this.reloadEvents();
                }
            );
        },
        updateReservationTitle(event) {
            this.backendLink.get(
                `reservation/updateTitle/${event.start}/${event.end}/${event.name}/${this.userEmail}/${this.calendarEmail}`,
                () => this.reloadEvents(),
                (message) => {
                    this.notify(message, 'error');
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
                        type: 'user availability',
                        editable: false,
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => this.notify(message, 'error')
            );
            this.backendLink.get(
                `user/listCommitments/${this.calendarEmail}/${this.userEmail}`,
                (response) => {
                    const mapped = response.data.map((availability) => ({
                        start: availability.startTime,
                        end: availability.endTime,
                        name: availability.title,
                        color: this.eventColor,
                        timed: true,
                        type: 'my reservation',
                        editable: true,
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => this.notify(message, 'error')
            );
            this.backendLink.get(
                `user/listAvailabilities/${this.userEmail}`,
                (response) => {
                    const mapped = response.data.map((availability) => ({
                        start: availability.startTime,
                        end: availability.endTime,
                        name: availability.title,
                        color: 'green',
                        timed: true,
                        type: 'my availability',
                        editable: true,
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => this.notify(message, 'error')
            );
        },
        backCallback() {
            this.$router.push({ name: 'my-availabilities' }).catch(() => {});
        },
    },
};
</script>
