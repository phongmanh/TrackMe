package com.manhnguyen.trackme.common

import androidx.annotation.NonNull
import io.reactivex.Scheduler

class SchedulerProvider(
    @NonNull val ioScheduler: Scheduler,
    @NonNull val mainThread: Scheduler,
    @NonNull val runOnUiThread: Scheduler,
    @NonNull val singleThread: Scheduler,
    @NonNull val trampolineThread: Scheduler
) {
}