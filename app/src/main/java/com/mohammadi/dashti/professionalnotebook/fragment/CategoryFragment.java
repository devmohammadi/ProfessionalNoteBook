package com.mohammadi.dashti.professionalnotebook.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Tasks;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.adapter.CategoryAdapter;
import com.mohammadi.dashti.professionalnotebook.model.Category;
import com.mohammadi.dashti.professionalnotebook.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CategoryAdapter.OnRecyclerItemClick {

    private RecyclerView recyclerViewCategoryItem;
    private CategoryAdapter categoryAdapter;
    private String category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerViewCategoryItem = view.findViewById(R.id.rvCategory);


        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("Programming", Constants.PROGRAMMING));
        categories.add(new Category("Cleaning", Constants.CLEANING));
        categories.add(new Category("Lesson", Constants.LESSON));
        categories.add(new Category("Movie", Constants.MOVIE));
        categories.add(new Category("Music", Constants.MUSIC));
        categories.add(new Category("Buy", Constants.BUY));
        categories.add(new Category("Other", Constants.OTHER));

        recyclerViewCategoryItem.setHasFixedSize(true);
        recyclerViewCategoryItem.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter(getContext(), categories);
        recyclerViewCategoryItem.setAdapter(categoryAdapter);
        categoryAdapter.setOnRecyclerItemClick(this);

        return view;
    }

    @Override
    public void onClick(Category mCategory) {
        category = mCategory.getTitle();

        handelFragment(new CategorySelectedFragment());
    }

    private void handelFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CATEGORY, category);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}