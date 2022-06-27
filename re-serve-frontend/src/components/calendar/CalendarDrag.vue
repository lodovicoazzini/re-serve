<template v-slot:event="{ event, timed, eventSummary }">
    <div>
        <div class="v-event-draggable" v-html="eventSummary()"></div>
        <div
            v-if="timed"
            class="v-event-drag-bottom"
            @mousedown.stop="extendBottom(event)"
        ></div>
    </div>
</template>

<script>
export default {
    computed: {
        calendarMethods: () => this.$parent.methods,
    },
    methods: {
        extendBottomCallback(event) {
            this.calendarMethods.createEvent = event;
            this.calendarMethods.createStart = event.start;
            this.calendarMethods.extendOriginal = event.end;
        },
    },
};
</script>

<style lang="scss">
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
