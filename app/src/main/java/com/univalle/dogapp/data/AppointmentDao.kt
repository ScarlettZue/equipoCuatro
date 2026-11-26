package com.univalle.dogapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.univalle.dogapp.model.Appointment

@Dao
interface AppointmentDao {
    /** Guarda la cita y devuelve el id autogenerado (el turno). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppointment(appointment: Appointment): Long

    @Query("SELECT * FROM Appointment ORDER BY id")
    suspend fun getListAppointments(): MutableList<Appointment>

    @Query("SELECT * FROM Appointment WHERE id = :id")
    suspend fun getAppointmentById(id: Int): Appointment?

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
}
