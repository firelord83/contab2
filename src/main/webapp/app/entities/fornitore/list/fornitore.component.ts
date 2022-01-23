import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFornitore } from '../fornitore.model';
import { FornitoreService } from '../service/fornitore.service';
import { FornitoreDeleteDialogComponent } from '../delete/fornitore-delete-dialog.component';

@Component({
  selector: 'jhi-fornitore',
  templateUrl: './fornitore.component.html',
})
export class FornitoreComponent implements OnInit {
  fornitores?: IFornitore[];
  isLoading = false;

  constructor(protected fornitoreService: FornitoreService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.fornitoreService.query().subscribe({
      next: (res: HttpResponse<IFornitore[]>) => {
        this.isLoading = false;
        this.fornitores = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IFornitore): number {
    return item.id!;
  }

  delete(fornitore: IFornitore): void {
    const modalRef = this.modalService.open(FornitoreDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.fornitore = fornitore;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
