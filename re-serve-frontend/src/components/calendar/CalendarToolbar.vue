<template>
    <v-sheet height="64">
        <v-toolbar flat>
            <v-btn
                outlined
                class="mr-4"
                color="grey darken-2"
                @click="setTodayCallback"
            >
                Today
            </v-btn>
            <v-btn
                fab
                text
                small
                color="grey darken-2"
                @click="previewsCallback"
            >
                <v-icon small> mdi-chevron-left </v-icon>
            </v-btn>
            <v-btn fab text small color="grey darken-2" @click="nextCallback">
                <v-icon small> mdi-chevron-right </v-icon>
            </v-btn>
            <v-toolbar-title v-if="this.$parent.$refs.calendar">
                {{ this.$parent.$refs.calendar.title }}
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-menu bottom right>
                <template v-slot:activator="{ on, attrs }">
                    <v-btn
                        outlined
                        color="grey darken-2"
                        v-bind="attrs"
                        v-on="on"
                    >
                        <span>{{
                            $parent.$data.typeToLabel[$parent.$data.type]
                        }}</span>
                        <v-icon right> mdi-menu-down </v-icon>
                    </v-btn>
                </template>
                <v-list>
                    <v-list-item @click="setCalendarType('day')">
                        <v-list-item-title>Day</v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="setCalendarType('week')">
                        <v-list-item-title>Week</v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="setCalendarType('month')">
                        <v-list-item-title>Month</v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="setCalendarType('4day')">
                        <v-list-item-title>4 days</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-toolbar>
    </v-sheet>
</template>

<script>
export default {
    emits: ['click-today', 'change-calendar-type'],
    methods: {
        setTodayCallback() {
            this.$emit('click-today');
        },
        previewsCallback() {
            this.$parent.$refs.calendar.prev();
        },
        nextCallback() {
            this.$parent.$refs.calendar.next();
        },
        setCalendarType(calendarType) {
            this.$emit('change-calendar-type', calendarType);
        },
    },
};
</script>
