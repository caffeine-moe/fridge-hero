package moe.caffeine.fridgehero.fridge

import android.content.Context
import android.widget.Toast
import moe.caffeine.fridgehero.MainViewModel
import moe.caffeine.fridgehero.model.FoodItem

fun handleBarcode(
    barcode: String,
    viewModel: MainViewModel,
    context: Context,
    onSuccess: (FoodItem) -> Unit
) {
    Toast.makeText(
        context,
        "Processing barcode $barcode, please wait...",
        Toast.LENGTH_SHORT
    ).show()
    viewModel.createFoodItemFromBarcode(barcode) { result ->
        result.fold(
            onFailure = { throwable ->
                Toast.makeText(
                    context,
                    "ERROR: ${throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onSuccess = { foodItem ->
                Toast.makeText(
                    context,
                    "Successfully scanned ${foodItem.name}!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.addToRealm(foodItem)
            }
        )
    }

}