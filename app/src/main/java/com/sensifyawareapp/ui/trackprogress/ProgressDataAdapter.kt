package com.sensifyawareapp.ui.trackprogress

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.sensifyawareapp.databinding.RowTrackBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ProgressDataAdapter(
    private val dataList: ArrayList<ProgressModel>,
) :
    RecyclerView.Adapter<ProgressDataAdapter.ViewHolder>() {

    class ViewHolder(view: View, val binding: RowTrackBinding) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowTrackBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            LinearLayoutCompat(viewGroup.context),
            false
        )
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val difference = dataList[position].endDate - dataList[position].startDate

        val days = (difference / (1000 * 60 * 60 * 24))
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
        val sec =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min) / 1000

        if (dataList[position].avgTime?.length == null){

            viewHolder.binding.avgResponseTime =
                "${
                    DecimalFormat("##.##").format(((min * 60) + sec).toFloat() / dataList[position].totalScore.toFloat())
                } Sec."
        } else{
            Log.e("Call " , " NOT Null")
            viewHolder.binding.avgResponseTime = dataList[position].avgTime
        }


        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dataList[position].startDate

        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        viewHolder.binding.date = formatter.format(calendar.time)
        viewHolder.binding.score = "${dataList[position].score}/${dataList[position].totalScore}"
        viewHolder.binding.isTraceAware = dataList[position].isTraceAware
        viewHolder.binding.isWordsAware = dataList[position].isWordsAware

        viewHolder.binding.avgTracingAccuracy = "${dataList[position].tracingAccuracy}%"
        viewHolder.binding.avgRecallAccuracy = "${dataList[position].recallAccuracy}%"

    }

    override fun getItemCount() = dataList.size

}
