package com.univalle.dogapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.univalle.dogapp.model.Appointment
import com.univalle.dogapp.utils.Constants.NAME_BD

@Database(entities = [Appointment::class], version = 1, exportSchema = false)
abstract class AppointmentDB : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var instance: AppointmentDB? = null

        // Una sola instancia para toda la app: abrir la base de datos es costoso.
        fun getDatabase(context: Context): AppointmentDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppointmentDB::class.java,
                    NAME_BD
                ).build().also { instance = it }
            }
        }
    }
}
