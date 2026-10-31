package com.haze.servicesprovider.data.repository

import com.haze.servicesprovider.data.remote.SupabaseClientProvider
import com.haze.servicesprovider.data.remote.dto.ServiceCategoryDto
import com.haze.servicesprovider.domain.model.ServiceCategory
import com.haze.servicesprovider.domain.repository.CategoryRepository
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val supabaseProvider: SupabaseClientProvider,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {

    private val defaultCategories = listOf(
        ServiceCategory("plumber", "Plumber", "Plumbing", true, 1),
        ServiceCategory("electrician", "Electrician", "ElectricBolt", true, 2),
        ServiceCategory("painter", "Painter", "FormatPaint", true, 3),
        ServiceCategory("carpenter", "Carpenter", "Carpenter", true, 4),
        ServiceCategory("ac_technician", "AC Technician", "AcUnit", true, 5),
        ServiceCategory("tv_repair", "TV Repair", "Tv", true, 6),
        ServiceCategory("refrigerator_repair", "Refrigerator Repair", "Kitchen", true, 7),
        ServiceCategory("washing_machine_repair", "Washing Machine Repair", "LocalLaundryService", true, 8),
        ServiceCategory("ro_repair", "RO Water Purifier Repair", "WaterDrop", true, 9),
        ServiceCategory("mobile_repair", "Mobile Repair", "Smartphone", true, 10),
        ServiceCategory("computer_repair", "Computer Repair", "Computer", true, 11),
        ServiceCategory("mechanic", "Mechanic", "Build", true, 12),
        ServiceCategory("cleaning_services", "Cleaning Services", "CleaningServices", true, 13),
        ServiceCategory("pest_control", "Pest Control", "PestControl", true, 14),
        ServiceCategory("salon_services", "Salon Services", "ContentCut", true, 15),
        ServiceCategory("appliance_repair", "Appliance Repair", "HomeRepairService", true, 16),
        ServiceCategory("other", "Other", "Category", true, 99)
    )

    override suspend fun getCategories(): Result<List<ServiceCategory>> = withContext(ioDispatcher) {
        runCatching {
            try {
                val dtoList = supabaseProvider.postgrest.from("service_categories")
                    .select {
                        filter {
                            eq("is_active", true)
                        }
                        order(column = "display_order", order = Order.ASCENDING)
                    }
                    .decodeList<ServiceCategoryDto>()

                if (dtoList.isNotEmpty()) {
                    dtoList.map { it.toDomain() }
                } else {
                    defaultCategories
                }
            } catch (_: Exception) {
                // If offline or table not yet seeded, return the complete default list
                defaultCategories
            }
        }
    }

    override fun getDefaultCategories(): List<ServiceCategory> = defaultCategories
}
