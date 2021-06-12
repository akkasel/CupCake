package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * This is a SharedViewModel for this app.
 * SharedViewModel is a single ViewModel that contains the data for more than 1 Fragments.
 * It's used to share data between Fragments.
 */

// to store the price per cupcake in a variable.
private const val PRICE_PER_CUPCAKE = 2.00

// to store the price of the Same Day Pick Up.
private const val PRICE_FOR_SAME_DAY_PICK_UP = 3.00

class OrderViewModel : ViewModel() {

    // Order Quantity (in private & (public) variable).
    private val _quantity = MutableLiveData<Int>(0)
    val quantity : LiveData<Int> = _quantity

    // Cupcake Flavor (in private & (public) variable).
    private val _flavor = MutableLiveData<String>("")
    val flavor : LiveData<String> = _flavor

    // Pick-Up Date (in private & public variable).
    private val _date = MutableLiveData<String>("")
    val date : LiveData<String> = _date

    // Total Price (in private & public variable)
    private val _price = MutableLiveData<Double>()
    // to convert the price to local currency format.
    val price : LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // a class property named 'dateOptions'
    val dateOptions = getPickupOptions()

    // Setter method. (this is a public method, Kotlin default visibility is public)
    // to set the Quantity
    fun setQuantity(numberCupcakes : Int) {
        _quantity.value = numberCupcakes
        updatePrice()
    }

    // to set the Flavor
    fun setFlavor(desiredFlavor : String) {
        _flavor.value = desiredFlavor
    }

    // to check if the flavor has been set or not.
    fun hasNoFlavorSet() : Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    // to set the Pick-up Date.
    fun setDate(pickUpDate : String) {
        _date.value = pickUpDate
        updatePrice()
    }

    // to create and return the list of Pickup dates.
    private fun getPickupOptions() : List<String> {
        // initialize a variable called 'options'
        val options = mutableListOf<String>()
        // create a date text formatter. 'E' = the day name, 'M' = month, 'd' is the day date. Ex: "Tue Dec 10"
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        // to contain the current date and time.
        val calendar = Calendar.getInstance()

        // build up a list of dates starting with the current date and the following 3 dates.
        // this 'repeat' block will format a date, add it to the list of date options,
        // and increment the calendar by 1 day.
        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }

        // return the updated 'options'
        return options
    }

    // to calculate the price of the Cupcake.
    private fun updatePrice() {
        // count the total price of the cupcake (quantity x price per cupcake)
        var calculatedPrice = (quantity.value ?:  0) * PRICE_PER_CUPCAKE

        // if the user choose same day pick-up (today), add 3.00 to the price.
        if (dateOptions[0] == _date.value) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICK_UP
        }
        _price.value = calculatedPrice
    }

    // to reset the Order ( / to reset the MutableLiveData properties in the view model)
    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    // to initialize the properties when an instance of OrderViewModel is created.
    init {
        resetOrder()
    }

}