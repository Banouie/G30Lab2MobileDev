//package com.g30lab3.app.Repositories
//
//class NoteRecyclerAdapter(val viewModel: MainViewModel, val arrayList: ArrayList<Blog>, val context: Context): RecyclerView.Adapter<NoteRecyclerAdapter.NotesViewHolder>() {
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int,
//    ): NoteRecyclerAdapter.NotesViewHolder {
//        var root = LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
//        return NotesViewHolder(root)
//    }
//
//    override fun onBindViewHolder(holder: NoteRecyclerAdapter.NotesViewHolder, position: Int) {
//        holder.bind(arrayList.get(position))
//    }
//
//    override fun getItemCount(): Int {
//        if(arrayList.size==0){
//            Toast.makeText(context,"List is empty",Toast.LENGTH_LONG).show()
//        }else{
//
//        }
//        return arrayList.size
//    }
//
//
//    inner  class NotesViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
//        fun bind(blog: Blog){
//            binding.title.text = blog.title
//            binding.delete.setOnClickListener {
//                viewModel.remove(blog)
//                notifyItemRemoved(arrayList.indexOf(blog))
//            }
//        }
//
//    }
//
//}