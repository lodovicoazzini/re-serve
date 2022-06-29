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
            events: [],
            eventColor: 'green',
        };
    },
    computed: {
        userEmail() {
            return this.$store.getters.userEmail;
        },
    },
    provide() {
        return {
            getEvents: () => this.events,
            saveEvent: this.saveAvailability,
            deleteEvent: this.deleteEvent,
        };
    },
    mounted() {
        this.reloadEvents();
    },
    methods: {
        saveAvailability(event) {
            this.backendLink.get(
                `availability/create/${event.start}/${event.end}/${this.userEmail}`,
                () => this.reloadEvents(),
                (message) => {
                    console.log(message);
                    this.reloadEvents();
                }
            );
        },
        deleteEvent(event) {
            if (event.type === 'availability') {
                this.backendLink.get(
                    `availability/remove/${event.start}/${event.end}/${this.userEmail}`,
                    () => this.reloadEvents(),
                    (message) => {
                        console.log(message);
                        this.reloadEvents();
                    }
                );
            } else {
                console.log(
                    `${event.type}/remove/${event.start}/${event.end}/${event.reservedBy.email}`
                );
                this.backendLink.get(
                    `${event.type}/remove/${event.start}/${event.end}/${event.reservedBy.email}`,
                    () => this.reloadEvents(),
                    (message) => {
                        console.log(message);
                        this.reloadEvents();
                    }
                );
            }
        },
        reloadEvents() {
            this.events = [];
            this.backendLink.get(
                `user/listAvailabilities/${this.userEmail}`,
                (response) => {
                    const mapped = response.data.map((availability) => ({
                        start: availability.startTime,
                        end: availability.endTime,
                        name: null,
                        color: this.eventColor,
                        timed: true,
                        editable: true,
                        type: 'availability',
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => console.log(message)
            );
            this.backendLink.get(
                `user/listCommitments/${this.userEmail}`,
                (response) => {
                    const mapped = response.data.map((commitment) => ({
                        start: commitment.startTime,
                        end: commitment.endTime,
                        name: null,
                        color: 'orange',
                        timed: true,
                        editable: true,
                        reservedBy: commitment.reservedBy,
                        type: 'reservation',
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => console.log(message)
            );
            this.backendLink.get(
                `user/listReservations/${this.userEmail}`,
                (response) => {
                    const mapped = response.data.map((commitment) => ({
                        start: commitment.startTime,
                        end: commitment.endTime,
                        name: null,
                        color: 'purple',
                        timed: true,
                        editable: true,
                        reservedBy: commitment.reservedBy,
                        type: 'reservation',
                    }));
                    this.events = this.events.concat(mapped);
                },
                (message) => console.log(message)
            );
        },
    },
};
</script>
