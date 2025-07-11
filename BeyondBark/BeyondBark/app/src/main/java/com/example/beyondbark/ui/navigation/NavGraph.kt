package com.example.beyondbark.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beyondbark.repository.NetmindRepository
import com.example.beyondbark.ui.about.AboutScreen
import com.example.beyondbark.ui.auth.LoginScreen
import com.example.beyondbark.ui.auth.RegisterScreen
import com.example.beyondbark.ui.chatbot.PetChatbotScreen
import com.example.beyondbark.ui.disease.DiseaseTextInputScreen
import com.example.beyondbark.ui.home.HomeScreen
import com.example.beyondbark.ui.moodDetection.PetMoodDetectionScreen
import com.example.beyondbark.ui.profile.EditProfileScreen
import com.example.beyondbark.ui.profile.ProfileScreen
import com.example.beyondbark.ui.splash.SplashScreen
import com.example.beyondbark.ui.welcome.WelcomeScreen
import com.example.beyondbark.ui.moodDetection.SuggestionsScreen
import com.example.beyondbark.ui.disease.DiseaseDetectionChoiceScreen
import com.example.beyondbark.ui.disease.DiseaseImageInputScreen
import com.example.beyondbark.ui.screen.SpeciesIdentificationScreen
import com.example.beyondbark.ui.rescue.RescueAbandonedPetsScreen
import com.example.beyondbark.ui.rescue.RegisterRescueScreen
import com.example.beyondbark.ui.rescue.RescuedAnimalsScreen
import com.example.beyondbark.ui.rescue.RescueRegistrationScreen
import com.example.beyondbark.viewmodel.RescueViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash"
) {
    NetmindRepository()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") { SplashScreen(navController) }
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController = navController) }
        composable("mood_detection") { PetMoodDetectionScreen(navController = navController) }

        // ✅ Mood Suggestions Screen route with "mood" argument
        composable("suggestionsScreen/{encodedMood}") { backStackEntry ->
            val encodedMood = backStackEntry.arguments?.getString("encodedMood")
            SuggestionsScreen(encodedMood, navController)
        }
        composable("disease_detection") { DiseaseDetectionChoiceScreen(navController) }
        composable("disease_by_image") { DiseaseImageInputScreen(navController) }
        composable("disease_by_text") { DiseaseTextInputScreen(navController) }
        composable("species_identification") { SpeciesIdentificationScreen(NetmindRepository()) }

        // Pass the viewModel to rescue screens
        composable("rescue_registration") {RescueRegistrationScreen(navController)}

        composable("rescue_abandoned_pets") {
            val rescueViewModel: RescueViewModel = viewModel()
            RescueAbandonedPetsScreen(viewModel = rescueViewModel)
        }
        composable("register_rescue") {
            val rescueViewModel: RescueViewModel = viewModel()
            RegisterRescueScreen(viewModel = rescueViewModel)
        }
        composable("rescued_animals") {
            val rescueViewModel: RescueViewModel = viewModel()
            RescuedAnimalsScreen(viewModel = rescueViewModel)
        }
        composable("about") {
            AboutScreen(navController)
        }
        composable("pet_chatbot") {
            PetChatbotScreen(navController)
        }




    }
}
