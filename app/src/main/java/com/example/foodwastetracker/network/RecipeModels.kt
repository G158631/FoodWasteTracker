package com.example.foodwastetracker.network

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    @SerializedName("servings")
    val servings: Int,
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("analyzedInstructions")
    val instructions: List<InstructionSet>? = null,
    @SerializedName("extendedIngredients")
    val ingredients: List<Ingredient>? = null
)

data class InstructionSet(
    @SerializedName("steps")
    val steps: List<InstructionStep>
)

data class InstructionStep(
    @SerializedName("number")
    val number: Int,
    @SerializedName("step")
    val step: String
)

data class Ingredient(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("original")
    val original: String
)

