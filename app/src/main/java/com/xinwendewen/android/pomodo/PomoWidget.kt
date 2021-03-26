package com.xinwendewen.android.pomodo

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import java.lang.UnsupportedOperationException

const val CLICK_ACTION = "com.xinwendewen.android.pomodo.CLICK"
const val RESET_ACTION = "com.xinwendewen.android.pomodo.RESET"
private const val TAG = "PomoWidget"
var isRunning = true

class PomoWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            CLICK_ACTION -> {
                val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                if (widgetId == -1) {
                    Log.w(TAG, "no appwidget id")
                } else {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    toggleTimer(context, appWidgetManager, widgetId)
                }
            }
            RESET_ACTION -> {
                val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                if (widgetId == -1) {
                    Log.w(TAG, "no appwidget id")
                } else {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    initTimer(context, appWidgetManager, widgetId)
                }
            }
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        if (appWidgetIds.size != 1) {
            throw UnsupportedOperationException("not support multiple host")
        }
        initTimer(context, appWidgetManager, appWidgetIds[0])
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun initTimer(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = createRemoteTimer(context).apply {
        setChronometer(
            R.id.pomo_timer,
            SystemClock.elapsedRealtime() + 60 * 1000 * 25,
            null,
            false
        )
        setOnClickPendingIntent(
            R.id.toggle,
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, PomoWidget::class.java).apply {
                    action = CLICK_ACTION
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        setOnClickPendingIntent(
            R.id.reset,
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, PomoWidget::class.java).apply {
                    action = RESET_ACTION
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun toggleTimer(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = createRemoteTimer(context).apply {
        setBoolean(R.id.pomo_timer, "setStarted", isRunning)
        isRunning = !isRunning
    }
    appWidgetManager.updateAppWidget(appWidgetId, views)
}


internal fun createRemoteTimer(context: Context) =
    RemoteViews(context.packageName, R.layout.pomo_widget)