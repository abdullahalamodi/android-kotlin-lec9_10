package com.abdullahalamodi.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
    val crimes = mutableListOf<Crime>();

    init {

        for (i in 0 until 10) {
            val crime = Crime();
            crime.title = "crime #$i";
            crime.isSolved = (i % 2 == 0);
            crimes += crime;
        }
        //serious crimes
        crimes[2].requiresPolice=true;
        crimes[5].requiresPolice=true;
    }
}