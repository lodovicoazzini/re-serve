<template>
    <v-col>
        <CalendarToolbar
            @click-today="focus = ''"
            @change-calendar-type="(calendarType) => (type = calendarType)"
        ></CalendarToolbar>
        <v-sheet height="600">
            <v-calendar
                ref="calendar"
                v-model="focus"
                color="primary"
                :events="events"
                :event-color="getEventColor"
                :type="type"
                @click:event="showEvent"
                @click:more="viewDay"
                @click:date="viewDay"
                @mousedown:event="startDrag"
                @mousedown:time="startTime"
                @mousemove:time="mouseMove"
                @mouseup:time="endDrag"
                @mouseleave.native="cancelDrag"
            >
                <template v-slot:day-body="{ date, week }">
                    <div
                        class="v-current-time"
                        :class="{ first: date === week[0].date }"
                        :style="{ top: nowY }"
                    ></div>
                </template>

                <template v-slot:event="{ event, timed, eventSummary }">
                    <div>
                        <div
                            class="v-event-draggable"
                            v-html="eventSummary()"
                        ></div>
                        <div
                            v-if="timed"
                            class="v-event-drag-bottom"
                            @mousedown.stop="extendBottom(event)"
                        ></div>
                    </div>
                </template>
            </v-calendar>
            <v-dialog v-model="eventIsSelected" persistent max-width="600px">
                <CalendarEventDetails
                    :selectedEvent="selectedEvent"
                    @close-dialog="selectedEvent = null"
                ></CalendarEventDetails
                >>
            </v-dialog>
        </v-sheet>
    </v-col>
</template>

<script>
import CalendarToolbar from '@/components/calendar/CalendarToolbar.vue';
import CalendarEventDetails from './CalendarEventDetails.vue';

export default {
    components: {
        CalendarToolbar,
        CalendarEventDetails,
    },
    props: {
        eventColor: {
            type: String,
            required: true,
        },
    },
    inject: ['saveEvent', 'deleteEvent', 'getEvents'],
    data: () => ({
        focus: '',
        type: 'week',
        typeToLabel: {
            month: 'Month',
            week: 'Week',
            day: 'Day',
            '4day': '4 Days',
        },
        selectedEvent: null,
        ready: false,
        dragEvent: null,
        dragStart: null,
        createEvent: null,
        createStart: null,
        extendOriginal: null,
    }),
    computed: {
        cal() {
            return this.ready ? this.$refs.calendar : null;
        },
        nowY() {
            return this.cal
                ? this.cal.timeToY(this.cal.times.now) + 'px'
                : '-10px';
        },
        eventIsSelected() {
            return this.selectedEvent != null;
        },
        events() {
            return this.getEvents();
        },
    },
    mounted() {
        this.$refs.calendar.checkChange();
        this.ready = true;
        this.scrollToTime();
        this.updateTime();
    },
    methods: {
        viewDay({ date }) {
            this.focus = date;
            this.type = 'day';
        },
        getEventColor(event) {
            return event.color;
        },
        showEvent({ event }) {
            if (event.editable) {
                this.selectedEvent = event;
            }
        },
        getCurrentTime() {
            return this.cal
                ? this.cal.times.now.hour * 60 + this.cal.times.now.minute
                : 0;
        },
        scrollToTime() {
            const time = this.getCurrentTime();
            const first = Math.max(0, time - (time % 30) - 30);
            this.cal.scrollToTime(first);
        },
        updateTime() {
            setInterval(() => this.cal.updateTimes(), 60 * 1000);
        },
        startDrag({ event, timed }) {
            if (event && timed && event.editable) {
                this.dragEvent = event;
                this.dragTime = null;
                this.extendOriginal = null;
            }
        },
        startTime(tms) {
            const mouse = this.toTime(tms);
            if (this.dragEvent && this.dragTime === null) {
                const start = this.dragEvent.start;
                this.dragTime = mouse - start;
            } else {
                this.createStart = this.roundTime(mouse);
                this.createEvent = {
                    name: `Event ${this.events.length}`,
                    color: this.eventColor,
                    start: this.createStart,
                    end: this.createStart,
                    timed: true,
                };
                this.events.push(this.createEvent);
            }
        },
        extendBottom(event) {
            if (event.editable) {
                this.createEvent = event;
                this.createStart = event.start;
                this.extendOriginal = event.end;
            }
        },
        mouseMove(tms) {
            const mouse = this.toTime(tms);
            if (this.createEvent && this.createStart !== null) {
                const mouseRounded = this.roundTime(mouse, false);
                const min = Math.min(mouseRounded, this.createStart);
                const max = Math.max(mouseRounded, this.createStart);
                this.createEvent.start = min;
                this.createEvent.end = max;
            }
        },
        endDrag() {
            if (this.createEvent !== null) {
                this.saveEvent(this.createEvent);
            }
            this.dragTime = null;
            this.dragEvent = null;
            this.createEvent = null;
            this.createStart = null;
            this.extendOriginal = null;
        },
        cancelDrag() {
            if (this.createEvent) {
                if (this.extendOriginal) {
                    this.createEvent.end = this.extendOriginal;
                } else {
                    const i = this.events.indexOf(this.createEvent);
                    if (i !== -1) {
                        this.events.splice(i, 1);
                    }
                }
            }
            this.createEvent = null;
            this.createStart = null;
            this.dragTime = null;
            this.dragEvent = null;
        },
        roundTime(time, down = true) {
            const roundTo = 15; // minutes
            const roundDownTime = roundTo * 60 * 1000;
            return down
                ? time - (time % roundDownTime)
                : time + (roundDownTime - (time % roundDownTime));
        },
        toTime(tms) {
            return new Date(
                tms.year,
                tms.month - 1,
                tms.day,
                tms.hour,
                tms.minute
            ).getTime();
        },
        rnd(a, b) {
            return Math.floor((b - a + 1) * Math.random()) + a;
        },
        rndElement(arr) {
            return arr[this.rnd(0, arr.length - 1)];
        },
    },
};
</script>

<style lang="scss">
.v-current-time {
    height: 2px;
    background-color: #ea4335;
    position: absolute;
    left: -1px;
    right: 0;
    pointer-events: none;
    &.first::before {
        content: '';
        position: absolute;
        background-color: #ea4335;
        width: 12px;
        height: 12px;
        border-radius: 50%;
        margin-top: -5px;
        margin-left: -6.5px;
    }
}
.v-event-draggable {
    padding-left: 6px;
}
.v-event-drag-bottom {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 4px;
    height: 4px;
    cursor: ns-resize;
    &::after {
        display: none;
        position: absolute;
        left: 50%;
        height: 4px;
        border-top: 1px solid white;
        border-bottom: 1px solid white;
        width: 16px;
        margin-left: -8px;
        opacity: 0.8;
        content: '';
    }
    &:hover::after {
        display: block;
    }
}
</style>
