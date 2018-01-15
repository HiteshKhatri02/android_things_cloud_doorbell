package com.ranosys.clouddoorbell


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.doorbell_entry.view.*
import android.R.attr.keySet
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.text.format.DateUtils
import com.bumptech.glide.Glide
import com.google.common.base.Splitter


/**
 * @author Ranosys Technologies
 */
class DoorbellAdapter(val mContext : Context,
                      var doorbellArrayList: MutableList<GetDataResponse>,
                      val listener: (View.OnClickListener)) : RecyclerView.Adapter<DoorbellAdapter.ViewHolder>() {


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

        fun bindItems(context: Context, doorbellEntry: GetDataResponse,listener:(View.OnClickListener) )  = with(itemView) {

            //itemView?.textView1?.text = getAnnotationString(convertToHashMap(doorbellEntry.annotations!!))
            itemView?.textView1?.text = convertToHashMap(doorbellEntry.annotations!!)?.keys.toString()
            itemView?.textView2?.text = getTime(context,doorbellEntry.timestamp)
            itemView?.setOnClickListener(listener)
        Glide.with(context)
                    .load(doorbellEntry.imgData)
                    .centerCrop()
                    .into(itemView?.imageView1)

        }

        fun getAnnotationString(annotations :MutableMap<String, Float>? ):String{
            return annotations.let {  transformAnnotation(it!!) }
        }

        fun transformAnnotation(annotations :MutableMap<String, Float>) : String{
            val keywords = ArrayList(annotations.keys)
            val limit:Int = Math.min(keywords.size,3)
            return TextUtils.join("\n",keywords.subList(0,limit))
        }

        fun getTime(context: Context, timeStamp: Long?) : CharSequence =
                DateUtils.getRelativeDateTimeString(
                        context.applicationContext, timeStamp!!,
                        DateUtils.SECOND_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,0)

        fun convertToHashMap(annotations: String): MutableMap<String, Float>? {
           if (!TextUtils.isEmpty(annotations)) {
               val stringForParse:String = annotations.replace("}","")
             val stringForParse2:String = stringForParse.replace("{","")
               val map = Splitter.on(", ").withKeyValueSeparator("=").split(stringForParse2)
               val flatMap: MutableMap<String, Float>? = mutableMapOf()
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                   map.forEach { t, u -> flatMap?.put(t, u.toFloat()) }
               }
               return flatMap
           }
           return null
        }
    }


    fun updateList(list:MutableList<GetDataResponse>){
        doorbellArrayList.clear()
        doorbellArrayList = list
        this.notifyDataSetChanged()
    }

}