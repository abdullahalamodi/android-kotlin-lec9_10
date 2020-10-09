package com.abdullahalamodi.criminalintent

import android.icu.text.DateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CrimeListFragment : Fragment() {
    private lateinit var crimeRecyclerView: RecyclerView;
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList());
    private var formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY)
    } else {
        null;  //for now no need to format date for old versions.
    };
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java);
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        crimeRecyclerView.layoutManager = LinearLayoutManager(context);
        crimeRecyclerView.adapter = adapter;
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                crimes?.let {
                    updateUI(crimes)
                }
            })
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment();
        }
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes);
        crimeRecyclerView.adapter = adapter;
    }

    //to format date
    fun dateFormat(date: Date): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatter?.run {
                format(date);
            }
        } else {
            date.toString();
        };
    }


    //view holders
    private abstract inner class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(crime: Crime)
    }

    private inner class CrimeHolder(itemView: View) : BaseHolder(itemView), View.OnClickListener {
        lateinit var crime: Crime;
        val titleTextView: TextView = itemView.findViewById(R.id.crime_title);
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date);

        init {
            itemView.setOnClickListener(this);
        }

        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = dateFormat(crime.date);
        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show();
        }

    }

    private inner class SeriousCrimeHolder(itemView: View) : BaseHolder(itemView) {
        lateinit var crime: Crime;
        val titleTextView: TextView = itemView.findViewById(R.id.crime_title);
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date);
        val imageView: ImageView = itemView.findViewById(R.id.crime_image);


        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = dateFormat(crime.date);
            imageView.setImageResource(R.drawable.ic_hand_shuck_24);
        }

        init {
            itemView.setOnClickListener {
                Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //adapter class
    private inner class CrimeAdapter(val crimes: List<Crime>) :
        RecyclerView.Adapter<BaseHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {

            return when (viewType) {
                1 -> {
                    val view = layoutInflater.inflate(R.layout.list_item_crime1, parent, false)
                    SeriousCrimeHolder(view);
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                    CrimeHolder(view);
                };
            }
        }

        override fun onBindViewHolder(holder: BaseHolder, position: Int) {
            val crime = crimes[position];
            holder.bind(crime);
        }

        override fun getItemCount() = crimes.size;

        override fun getItemViewType(position: Int): Int {
            return when (crimes[position].requiresPolice) {
                true -> 1; // crime is requiresPolice;
                false -> 0; // crime is not requiresPolice;
            }
        }

    }
}