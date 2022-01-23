import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFatturePassivo } from '../fatture-passivo.model';
import { FatturePassivoService } from '../service/fatture-passivo.service';
import { FatturePassivoDeleteDialogComponent } from '../delete/fatture-passivo-delete-dialog.component';

@Component({
  selector: 'jhi-fatture-passivo',
  templateUrl: './fatture-passivo.component.html',
})
export class FatturePassivoComponent implements OnInit {
  fatturePassivos?: IFatturePassivo[];
  isLoading = false;

  constructor(protected fatturePassivoService: FatturePassivoService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.fatturePassivoService.query().subscribe({
      next: (res: HttpResponse<IFatturePassivo[]>) => {
        this.isLoading = false;
        this.fatturePassivos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IFatturePassivo): number {
    return item.id!;
  }

  delete(fatturePassivo: IFatturePassivo): void {
    const modalRef = this.modalService.open(FatturePassivoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.fatturePassivo = fatturePassivo;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
