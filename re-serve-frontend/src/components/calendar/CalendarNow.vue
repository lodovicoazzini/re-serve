<template v-slot:day-body="{ date, week }">
    <div
        class="v-current-time"
        :class="{ first: date === week[0].date }"
        :style="{ top: nowY }"
    ></div>
</template>

<script>
export default {};
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
</style>
