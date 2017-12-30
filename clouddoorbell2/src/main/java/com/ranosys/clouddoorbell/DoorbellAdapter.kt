package com.ranosys.clouddoorbell


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.doorbell_entry.view.*
import android.R.attr.keySet
import android.content.Context
import android.text.TextUtils
import android.text.format.DateUtils
import com.bumptech.glide.Glide


/**
 * @author Ranosys Technologies
 */
class DoorbellAdapter(val mContext : Context, val doorbellArrayList: MutableList<DoorbellEntry>, val listener: (DoorbellEntry) -> Unit) : RecyclerView.Adapter<DoorbellAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(mContext, doorbellArrayList.get(position),listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.doorbell_entry,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return doorbellArrayList.size
    }


    class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, doorbellEntry: DoorbellEntry,listener:(DoorbellEntry) -> Unit)  = with(itemView) {

            itemView?.textView1?.text = getAnnotationString(doorbellEntry.annotations)
            itemView?.textView2?.text = getTime(context,doorbellEntry.timestamp)

            Glide.with(context)
                    .load(doorbellEntry.image)
                    .centerCrop()
                    .into(itemView?.imageView1);

        }

        fun getAnnotationString(annotations :MutableMap<String, Float>? ):String{
            return annotations.let {  transformAnnotation(it!!) }  ?: "No annotation yet."
        }

        fun transformAnnotation(annotations :MutableMap<String, Float>) : String{
            val keywords = ArrayList(annotations.keys)
            val limit:Int = Math.min(keywords.size,3)
            return TextUtils.join("\n",keywords.subList(0,limit))
        }

        fun getTime(context: Context, timeStamp: Long) : CharSequence =
                DateUtils.getRelativeDateTimeString(
                        context.applicationContext, timeStamp,
                        DateUtils.SECOND_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,0) }

}