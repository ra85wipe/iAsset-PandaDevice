/*
 * util.h
 *
 *  Created on: 06.03.2019
 *      Author: psota
 */

#include <iostream>

#include <vector>
#include <unordered_map>

// This file defines std::ostream << printers for some basic types

#ifndef PRINTER_H
#define PRINTER_H

template <typename T>
std::ostream & operator<<(std::ostream & os, const std::vector<T>& vector) 
{
	os << "[";
	for (const auto & element : vector){
		os << element;
		os << ",";
	};
	os << "]";

	return os;
}

template <typename K, typename V>
std::ostream & operator<<(std::ostream & os, const std::unordered_map<K,V> & unordered_map)
{
	// Just list keys, here
	os << "[";
	//for (const auto & element : unordered_map) {
		//os << element.first;
		os << ",";
	//};
	os << "]";

	return os;
}

#endif