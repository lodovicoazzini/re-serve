<template>
    <v-row class="fill-height">
        <MyCalendar></MyCalendar>
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
    components: { MyCalendar },
    methods: {
        backCallback() {
            this.$router.push({ name: 'my-availabilities' }).catch(() => {});
        },
    },
};
</script>
