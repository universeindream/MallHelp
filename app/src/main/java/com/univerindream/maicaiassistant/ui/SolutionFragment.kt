package com.univerindream.maicaiassistant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.GsonUtils
import com.elvishew.xlog.XLog
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.univerindream.maicaiassistant.MCStep
import com.univerindream.maicaiassistant.MHConfig
import com.univerindream.maicaiassistant.R
import com.univerindream.maicaiassistant.databinding.FragmentSolutionBinding
import com.univerindream.maicaiassistant.databinding.ItemStepBinding

class SolutionFragment : Fragment() {
    private var _binding: FragmentSolutionBinding? = null

    private val binding get() = _binding!!

    private val itemAdapter by lazy {
        ItemAdapter<BindingStepItem>()
    }

    private val fastAdapter by lazy {
        FastAdapter.with(itemAdapter)
    }

    private val mcSolution by lazy {
        MHConfig.curMCSolution
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("updateStep") { requestKey, bundle ->
            XLog.i("handle -> $requestKey $bundle")
            val mcStep = GsonUtils.fromJson(bundle.getString("stepJson"), MCStep::class.java)
            val index = bundle.getInt("stepIndex")
            mcSolution.steps[index] = mcStep
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSolutionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.solutionSteps.adapter = fastAdapter
        binding.solutionSteps.layoutManager = LinearLayoutManager(requireContext())

        fastAdapter.onClickListener = { _, _, _, position ->
            val stepJson = GsonUtils.toJson(mcSolution.steps[position])
            val action = SolutionFragmentDirections.actionSolutionFragmentToStepFragment(
                stepJson = stepJson,
                stepIndex = position
            )
            findNavController().navigate(action)
            false
        }

        binding.floatingActionButton.setOnClickListener {
            saveData()
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        binding.solutionNameValue.text = mcSolution.name
        itemAdapter.clear()
        mcSolution.steps.forEachIndexed { index, mcStep ->
            itemAdapter.add(BindingStepItem(mcStep).apply {
                tag = index + 1
            })
        }
    }

    fun saveData() {
        MHConfig.curMCSolution = mcSolution
        findNavController().navigateUp()
    }

    class BindingStepItem(model: MCStep) : ModelAbstractBindingItem<MCStep, ItemStepBinding>(model) {

        override val type: Int
            get() = R.id.adapter_step_item

        override fun bindView(binding: ItemStepBinding, payloads: List<Any>) {
            val content = "Step$tag: ${model.name}"
            binding.solutionStepName.text = content

            var handleContent = "  - 操作:"
            handleContent += "\n      - 类型：${model.handle.type.to2String()}"
            binding.solutionStepHandle.text = handleContent

            var condContent = "  - 条件:"
            model.condList.forEachIndexed { i, s ->
                condContent += "\n      - 条件${i + 1}:"
                condContent += "\n         - 类型: " + s.type.to2String()
            }
            binding.solutionStepCondList.text = condContent
        }

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemStepBinding {
            return ItemStepBinding.inflate(inflater, parent, false)
        }
    }

}