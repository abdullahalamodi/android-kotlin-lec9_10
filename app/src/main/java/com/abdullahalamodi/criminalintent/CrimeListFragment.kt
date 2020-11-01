package com.abdullahalamodi.criminalintent

import android.content.Context
import android.icu.text.DateFormat
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*

class CrimeListFragment : Fragment() {
    private var callbacks: Callbacks? = null;
    private lateinit var crimeRecyclerView: RecyclerView;
    private lateinit var clueGroup: Group;
    private lateinit var addBtn: ImageButton;
    private var adapter: CrimeAdapter? = CrimeAdapter();
    private var formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY)
    } else {
        null;  //for now no need to format date for old versions.
    };
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java);
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        clueGroup = view.findViewById(R.id.add_clue_group);
        addBtn = view.findViewById(R.id.add_btn);

        // add button for first crime
        addBtn.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                crimes?.let {
                    if (crimes.isNotEmpty()) {
                        setAddClueViability(View.GONE);
                        updateUI(crimes)
                    } else {
                        setAddClueViability(View.VISIBLE);
                    }
                }
            })
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment();
        }
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter();
        adapter?.submitList(crimes);
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

    /* private inner class CrimeHolder(itemView: View) : BaseHolder(itemView), View.OnClickListener {
         private lateinit var crime: Crime;
         private val titleTextView: TextView = itemView.findViewById(R.id.crime_title);
         private val dateTextView: TextView = itemView.findViewById(R.id.crime_date);


         init {
             itemView.setOnClickListener(this);
         }

         override fun bind(crime: Crime) {
             this.crime = crime
             titleTextView.text = crime.title
             dateTextView.text = dateFormat(crime.date);

         }

         override fun onClick(v: View?) {
             //Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show();

         }

     }*/

    private inner class SeriousCrimeHolder(itemView: View) : BaseHolder(itemView) {
        lateinit var crime: Crime;
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title);
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date);
        private val imageTextView: ImageView = itemView.findViewById(R.id.crime_photo);
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        private lateinit var photoFile: File;


        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = dateFormat(crime.date);
            imageTextView.apply {
                photoFile = crimeListViewModel.getPhotoFile(crime)
                if (photoFile.exists()) {
                    val bitmap = getScaledBitmap(photoFile.path, requireActivity())
                    this.setImageBitmap(bitmap)
                } else {
                    this.setImageDrawable(null)
                }
            }
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        init {
            itemView.setOnClickListener {
//                Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show();
                callbacks?.onCrimeSelected(crime.id);
            }
        }
    }


    //adapter class
    private inner class CrimeAdapter() :
        ListAdapter<Crime, BaseHolder>(CrimeDiffUtil()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {

//            return when (viewType) {
//                1 -> {
//                    val view = layoutInflater.inflate(R.layout.list_item_crime1, parent, false)
//                    SeriousCrimeHolder(view);
//                }
//                else -> {
//                    val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
//                    CrimeHolder(view);
//                };
//            }

            val view = layoutInflater.inflate(R.layout.list_item_crime1, parent, false)
            return SeriousCrimeHolder(view);
        }

        override fun onBindViewHolder(holder: BaseHolder, position: Int) {
            val crime = getItem(position);
            holder.bind(crime);
        }

        // override fun getItemCount() = crimes.size;

//        override fun getItemViewType(position: Int): Int {
//            return when (crimes[position].requiresPolice) {
//                true -> 1; // crime is requiresPolice;
//                false -> 0; // crime is not requiresPolice;
//            }
//        }

    }

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    //menu inflate
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setAddClueViability(visible: Int) {
        clueGroup.visibility = visible;
    }

    class CrimeDiffUtil : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id === newItem.id;
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem;
        }

    }
}

