@echo off
set GNUPLOT_DIR=D:\Escritorio\gnuplot\binary
%GNUPLOT_DIR%\gnuplot chart.gp.txt
latex Informe.tex
dvipdfm Informe.dvi